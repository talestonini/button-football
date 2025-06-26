package com.talestonini.service

import com.talestonini.datastructure.Tree
import com.talestonini.service.ValidationService.Companion.validateSetOfChampionshipFinalsMatches
import com.talestonini.service.ValidationService.Companion.validateSetOfChampionshipGroupStandings
import com.talestonini.service.ValidationService.Companion.validateSetOfSingleGroupMatches
import kotlin.math.ln
import kotlin.math.pow
import kotlin.random.Random

class ManagementService {
    companion object {

        fun matchStandings(match: Match): Pair<Standing, Standing> {
            /**
             * Obtains points, wins, draws and losses for a team at a match.  In a single match, obviously, the number
             * of wins, draws and losses can only be 1.
             */
            data class WDL(val wins: Int, val draws: Int, val losses: Int)
            fun winsDrawsLosses(match: Match, team: Team): WDL =
                when (match.winner(isIncludeExtraTimeAndPenaltyShootouts = false)) {
                    team -> WDL(1, 0, 0)
                    null -> WDL(0, 1, 0)
                    else -> WDL(0, 0, 1)
                }

            val teamAWdl = winsDrawsLosses(match, match.teamA)
            val teamAStanding = Standing(
                match.championship, match.teamA, match.type, null, null, null, teamAWdl.wins, teamAWdl.draws,
                teamAWdl.losses, match.numGoalsTeamA ?: 0, match.numGoalsTeamB ?: 0, isIgpUntiedByHeadToHead = false,
                isIgpUntiedRandomly = false, isEgpUntiedRandomly = false, isFpUntiedRandomly = false
            )

            val teamBWdl = winsDrawsLosses(match, match.teamB)
            val teamBStanding = Standing(
                match.championship, match.teamB, match.type, null, null, null, teamBWdl.wins, teamBWdl.draws,
                teamBWdl.losses, match.numGoalsTeamB ?: 0, match.numGoalsTeamA ?: 0, isIgpUntiedByHeadToHead = false,
                isIgpUntiedRandomly = false, isEgpUntiedRandomly = false, isFpUntiedRandomly = false
            )

            return Pair(teamAStanding, teamBStanding)
        }

        private fun isTiedByAllCriteria(st1: Standing, st2: Standing): Boolean =
            st1.numPoints() == st2.numPoints() &&
                    st1.numWins == st2.numWins &&
                    st1.numGoalsDiff() == st2.numGoalsDiff() &&
                    st1.numGoalsScored == st2.numGoalsScored

        private fun isTiedByAllCriteria(st1: MatchWithCampaignStanding, st2: MatchWithCampaignStanding): Boolean =
            st1.matchStanding.numPoints() == st2.matchStanding.numPoints() &&
                    st1.matchStanding.numGoalsDiff() == st2.matchStanding.numGoalsDiff() &&
                    st1.matchStanding.numGoalsScored == st2.matchStanding.numGoalsScored &&
                    st1.campaignStanding.numPoints() == st2.campaignStanding.numPoints() &&
                    st1.campaignStanding.numWins == st2.campaignStanding.numWins &&
                    st1.campaignStanding.numGoalsDiff() == st2.campaignStanding.numGoalsDiff() &&
                    st1.campaignStanding.numGoalsScored == st2.campaignStanding.numGoalsScored

        /**
         * Processes intra-group standings for a single group of a championship, given the set of 6 matches of that
         * group.
         */
        fun processIntraGroupStandings(groupMatches: Set<Match>): Set<Standing> {
            validateSetOfSingleGroupMatches(groupMatches)

            // get match standings for each match
            val standings: List<Standing> = groupMatches.flatMap { matchStandings(it).toList() }

            // group by team and compute totals by reducing each team's standings
            val reducedStandings = standings.groupBy { it.team }
                .mapValues {
                    it.value.reduce { acc, st ->
                        Standing(
                            acc.championship, acc.team, acc.type, null, null, null,
                            acc.numWins + st.numWins,
                            acc.numDraws + st.numDraws,
                            acc.numLosses + st.numLosses,
                            acc.numGoalsScored + st.numGoalsScored,
                            acc.numGoalsConceded + st.numGoalsConceded,
                            isIgpUntiedByHeadToHead = false, isIgpUntiedRandomly = false, isEgpUntiedRandomly = false,
                            isFpUntiedRandomly = false
                        )
                    }
                }
                .map { it.value }  // discard the key created in groupBy

            // sort and detect possible ties by all criteria
            return reducedStandings.sortedWith(
                compareBy(
                    Standing::numPoints,
                    Standing::numWins,
                    Standing::numGoalsDiff,
                    Standing::numGoalsScored
                )
                    .reversed()
            )
                .withIndex()  // obtain a position number (0, 1, 2, 3)
                .zipWithNext()  // pair adjacent teams standings ((0,1), (1,2), (2,3))
                .flatMap { processPossiblyTiedIntraGroupStandings(it, groupMatches) }
                .groupBy { it.team }  // standings in positions 2 and 3 will have duplicates, as per zipWithNext
                .mapValues {
                    it.value.reduce { acc, st ->
                        if (acc.isIgpUntiedByHeadToHead && !st.isIgpUntiedByHeadToHead) acc
                        else if (!acc.isIgpUntiedByHeadToHead && st.isIgpUntiedByHeadToHead) st
                        else if (acc.isIgpUntiedRandomly && !st.isIgpUntiedRandomly) acc
                        else if (!acc.isIgpUntiedRandomly && st.isIgpUntiedRandomly) st
                        else acc  // either would be fine, as they are tied by all criteria, even head-to-head match
                    }
                }
                .map { it.value }  // discard the key created in groupBy and return
                .sortedWith(compareBy(Standing::numIntraGrpPos))  // sort again, as ties may have been broken
                .toSet()
        }

        private fun processPossiblyTiedIntraGroupStandings(
            it: Pair<IndexedValue<Standing>, IndexedValue<Standing>>,
            groupMatches: Set<Match>,
        ): List<Standing> {
            fun findMatchBetweenTeams(teamA: Team, teamB: Team): Match =
                groupMatches.first {
                    ((it.teamA.name == teamA.name && it.teamB.name == teamB.name) ||
                            (it.teamA.name == teamB.name && it.teamB.name == teamA.name))
                }

            val isTied = isTiedByAllCriteria(it.first.value, it.second.value)

            // to try and break the tie with the head-to-head match result
            val winnerOfHeadToHeadMatch =
                if (isTied) findMatchBetweenTeams(it.first.value.team, it.second.value.team).winner()
                else null

            return if (!isTied)
            // standings are not tied or cannot be untied by the head-to-head match
                listOf(
                    Standing(it.first.value, it.first.index + 1, null, null),
                    Standing(it.second.value, it.second.index + 1, null, null)
                )
            else if (winnerOfHeadToHeadMatch == null) {
                val isFirstTeamToComeFirst = Random.nextBoolean()
                val firstTeamPos = if (isFirstTeamToComeFirst) it.first.index else it.second.index
                val secondTeamPos = if (isFirstTeamToComeFirst) it.second.index else it.first.index
                listOf(
                    Standing(it.first.value, firstTeamPos + 1, null, null, isIgpUntiedRandomly = true),
                    Standing(it.second.value, secondTeamPos + 1, null, null, isIgpUntiedRandomly = true)
                )
            } else
            // standings are tied, but can be untied by the head-to-head match
                if (winnerOfHeadToHeadMatch == it.first.value.team) {
                    // keep order of both standings
                    listOf(
                        Standing(it.first.value, it.first.index + 1, null, null, isIgpUntiedByHeadToHead = true),
                        Standing(it.second.value, it.second.index + 1, null, null, isIgpUntiedByHeadToHead = true)
                    )
                } else {
                    // swap order of both standings
                    listOf(
                        Standing(it.second.value, it.first.index + 1, null, null, isIgpUntiedByHeadToHead = true),
                        Standing(it.first.value, it.second.index + 1, null, null, isIgpUntiedByHeadToHead = true)
                    )
                }
        }

        /**
         * Processes extra-group standings for a championship, given the group stage standings of all groups.
         */
        fun processExtraGroupStandings(groupStandings: Set<Standing>): Set<Standing> {
            validateSetOfChampionshipGroupStandings(groupStandings)
            // group standings have intra-group ordering
            groupStandings.all { it.numIntraGrpPos != null }

            /**
             * @param standings: the intra group standings to sort (firsts of each group, then seconds, etc)
             * @param startingPos: because all first teams of each group come before the seconds, which come before the
             * thirds and so forth
             */
            fun sortStandingsOfSameIntraGroupPosition(standings: Set<Standing>, startingPos: Int): Set<Standing> {
                return standings.sortedWith(
                    compareBy(
                        Standing::numPoints,
                        Standing::numWins,
                        Standing::numGoalsDiff,
                        Standing::numGoalsScored
                    )
                        .reversed()
                )
                    .withIndex()  // obtain a position number (0, 1, 2, 3)
                    .zipWithNext()  // pair adjacent teams standings ((0,1), (1,2), (2,3))
                    .flatMap { processPossiblyTiedExtraGroupStandings(it, startingPos) }
                    .groupBy { it.team }  // standings in positions 2 and 3 will have duplicates, as per zipWithNext
                    .mapValues {
                        it.value.reduce { acc, st ->
                            if (acc.isEgpUntiedRandomly && !st.isEgpUntiedRandomly) acc
                            else if (!acc.isEgpUntiedRandomly && st.isEgpUntiedRandomly) st
                            else acc  // either would be fine, as they are tied by all criteria
                        }
                    }
                    .map { it.value }  // discard the key created in groupBy and return
                    .sortedWith(compareBy(Standing::numExtraGrpPos))  // sort again, as ties may have been broken
                    .toSet()
            }

            val numGroups = groupStandings.first().championship.numTeams / Constants.NUM_TEAMS_PER_GROUP
            return (1..Constants.NUM_TEAMS_PER_GROUP).map { igp ->
                sortStandingsOfSameIntraGroupPosition(
                    groupStandings.filter { it.numIntraGrpPos == igp }.toSet(),
                    (igp - 1) * numGroups + 1
                )
            }.flatten().toSet()
        }

        private fun processPossiblyTiedExtraGroupStandings(
            it: Pair<IndexedValue<Standing>, IndexedValue<Standing>>,
            startingPos: Int
        ): List<Standing> =
            if (!isTiedByAllCriteria(it.first.value, it.second.value))
                listOf(
                    Standing(
                        it.first.value, it.first.value.numIntraGrpPos, it.first.index + startingPos, null,
                        it.first.value.isIgpUntiedByHeadToHead, it.first.value.isIgpUntiedRandomly
                    ),
                    Standing(
                        it.second.value, it.second.value.numIntraGrpPos, it.second.index + startingPos, null,
                        it.second.value.isIgpUntiedByHeadToHead, it.second.value.isIgpUntiedRandomly
                    ),
                )
            else {
                val isFirstTeamToComeFirst = Random.nextBoolean()
                val firstTeamPos = if (isFirstTeamToComeFirst) it.first.index else it.second.index
                val secondTeamPos = if (isFirstTeamToComeFirst) it.second.index else it.first.index
                listOf(
                    Standing(
                        it.first.value, it.first.value.numIntraGrpPos, firstTeamPos + startingPos, null,
                        it.first.value.isIgpUntiedByHeadToHead, it.first.value.isIgpUntiedRandomly,
                        isEgpUntiedRandomly = true
                    ),
                    Standing(
                        it.second.value, it.second.value.numIntraGrpPos, secondTeamPos + startingPos, null,
                        it.second.value.isIgpUntiedByHeadToHead, it.second.value.isIgpUntiedRandomly,
                        isEgpUntiedRandomly = true
                    )
                )
            }

        /**
         * Processes the final standings for a championship, given the extra-group standings of the championship and its
         * finals matches.
         */
        fun processFinalStandings(groupStandings: Set<Standing>, finalsMatches: Set<Match>): Set<Standing> {
            validateSetOfChampionshipGroupStandings(groupStandings)
            validateSetOfChampionshipFinalsMatches(finalsMatches)
            // standings and matches are of the same championship
            assert(groupStandings.first().championship == finalsMatches.first().championship)
            // standings have intra-group and extra-group ordering
            groupStandings.all { it.numIntraGrpPos != null && it.numExtraGrpPos != null }
            // TODO: confirm this validation is needed
            // finals matches have been played
            finalsMatches.all { it.matchState() == MatchState.PLAYED }

            // build funneling tree and find 3rd place playoff
            val funnelingTree = buildFunnelingTree(groupStandings, finalsMatches)
            val thirdPlacePlayoff = findThirdPlacePlayoff(funnelingTree, finalsMatches)
            val finalStandingMatchType = funnelingTree.value().match?.type

            // init set to return
            val finalStandings: MutableSet<Standing> = emptySet<Standing>().toMutableSet()

            // bottom final standings are a copy of the extra-group standings
            val championship = finalsMatches.first().championship
            for (i in championship.numTeams downUntil championship.numQualif) {
                val gs = groupStandings.find { it.numExtraGrpPos == i }
                assert(gs != null)
                finalStandings +=
                    Standing(
                        gs!!, finalStandingMatchType!!, gs.numIntraGrpPos, gs.numExtraGrpPos, gs.numExtraGrpPos,
                        gs.isIgpUntiedByHeadToHead, gs.isIgpUntiedRandomly, gs.isEgpUntiedRandomly
                    )
            }

            // get match standings for each finals match
            val finalsMatchesStandings: List<Standing> = finalsMatches.flatMap { matchStandings(it).toList() }

            // group by team and compute totals by reducing each team's standings
            val reducedStandings = groupStandings.filter { it.numExtraGrpPos!! <= championship.numQualif }
                .plus(finalsMatchesStandings)
                .groupBy { it.team }
                .mapValues {
                    it.value.reduce { acc, st ->
                        Standing(
                            acc.championship, acc.team, finalStandingMatchType!!,
                            reduceInts(acc.numIntraGrpPos, st.numIntraGrpPos),
                            reduceInts(acc.numExtraGrpPos, st.numExtraGrpPos),
                            reduceInts(acc.numFinalPos, st.numFinalPos),
                            acc.numWins + st.numWins,
                            acc.numDraws + st.numDraws,
                            acc.numLosses + st.numLosses,
                            acc.numGoalsScored + st.numGoalsScored,
                            acc.numGoalsConceded + st.numGoalsConceded,
                            acc.isIgpUntiedByHeadToHead || st.isIgpUntiedByHeadToHead,
                            acc.isIgpUntiedRandomly || st.isIgpUntiedRandomly,
                            acc.isEgpUntiedRandomly || st.isEgpUntiedRandomly,
                            acc.isFpUntiedRandomly || st.isFpUntiedRandomly
                        )
                    }
                }
                .map { it.value }  // discard the key created in groupBy

            // traverse the funneling tree for the top final standings
            val funnelingList = funnelingTree.toList()
            for (level in funnelingTree.depth() downTo 1) {
                if (level > 2) {  // from quarter-finals onwards
                    // get match standings for the losers
                    val losersMatchWithCampaignStandings = funnelingList.filter { it.level == level }
                        .flatMap { matchStandings(it.match!!).toList().filter {
                            standing -> standing.team == it.match?.loser()
                        }}
                        .map { MatchWithCampaignStanding(it, reducedStandings.find { rs -> rs.team == it.team }!!) }

                    // sort losers, as they did not progress to the next level
                    finalStandings +=
                        losersMatchWithCampaignStandings.sortedWith(
                            compareBy<MatchWithCampaignStanding> { it.matchStanding.numPoints() }
                                .thenBy { it.matchStanding.numGoalsDiff() }
                                .thenBy { it.matchStanding.numGoalsScored }
                                .thenBy { it.campaignStanding.numPoints() }
                                .thenBy { it.campaignStanding.numWins }
                                .thenBy { it.campaignStanding.numGoalsDiff() }
                                .thenBy { it.campaignStanding.numGoalsScored }
                                .reversed()
                        )
                            .withIndex()  // obtain a position number (0, 1, 2, 3)
                            .zipWithNext()  // pair adjacent teams standings ((0,1), (1,2), (2,3))
                            .flatMap { processPossiblyTiedFinalStandings(it, (2.0.pow(level - 1) + 1).toInt()) }
                            .groupBy { it.team }  // standings in positions 2 and 3 will have duplicates,
                                                  // as per zipWithNext
                            .mapValues {
                                it.value.reduce { acc, st ->
                                    if (acc.isFpUntiedRandomly && !st.isFpUntiedRandomly) acc
                                    else if (!acc.isFpUntiedRandomly && st.isFpUntiedRandomly) st
                                    else acc  // either would be fine, as they are tied by all criteria
                                }
                            }
                            .map { it.value }  // discard the key created in groupBy and return
                } else {
                    // third-place playoff match
                    finalStandings += buildFinalStanding(
                        reducedStandings.find { it.team == thirdPlacePlayoff?.loser() }!!, 4
                    )
                    finalStandings += buildFinalStanding(
                        reducedStandings.find { it.team == thirdPlacePlayoff?.winner() }!!, 3
                    )

                    // grand final
                    finalStandings += buildFinalStanding(
                        reducedStandings.find { it.team == funnelingTree.value().match?.loser() }!!, 2
                    )
                    finalStandings += buildFinalStanding(
                        reducedStandings.find { it.team == funnelingTree.value().match?.winner() }!!, 1
                    )
                }
            }

            return finalStandings
                .sortedWith(compareBy(Standing::numFinalPos))  // sort again, as ties may have been broken
                .toSet()
        }

        private data class MatchWithCampaignStanding(val matchStanding: Standing, val campaignStanding: Standing)

        private infix fun Int.downUntil(until: Int) = IntProgression.fromClosedRange(this, until + 1, -1)

        private fun buildFinalStanding(toClone: Standing, finalPos: Int): Standing =
            Standing(toClone, toClone.numIntraGrpPos, toClone.numExtraGrpPos, finalPos, toClone.isIgpUntiedByHeadToHead,
                toClone.isIgpUntiedRandomly, toClone.isEgpUntiedRandomly)

        private fun processPossiblyTiedFinalStandings(
            it: Pair<IndexedValue<MatchWithCampaignStanding>, IndexedValue<MatchWithCampaignStanding>>,
            startingPos: Int
        ): List<Standing> {
            val teamOneStats = it.first.value.campaignStanding
            val teamTwoStats = it.second.value.campaignStanding
            return if (!isTiedByAllCriteria(it.first.value, it.second.value)) {
                // original sorting by the level final match and campaign is able to sort the final pos
                listOf(
                    Standing(
                        teamOneStats, teamOneStats.numIntraGrpPos, teamOneStats.numExtraGrpPos,
                        it.first.index + startingPos, teamOneStats.isIgpUntiedByHeadToHead,
                        teamOneStats.isIgpUntiedRandomly, teamOneStats.isEgpUntiedRandomly
                    ),
                    Standing(
                        teamTwoStats, teamTwoStats.numIntraGrpPos, teamTwoStats.numExtraGrpPos,
                        it.second.index + startingPos, teamTwoStats.isIgpUntiedByHeadToHead,
                        teamTwoStats.isIgpUntiedRandomly, teamTwoStats.isEgpUntiedRandomly
                    )
                )
            } else {
                // final pos will be decided randomly
                val isFirstTeamToComeFirst = Random.nextBoolean()
                val firstTeamPos = if (isFirstTeamToComeFirst) it.first.index else it.second.index
                val secondTeamPos = if (isFirstTeamToComeFirst) it.second.index else it.first.index
                listOf(
                    Standing(
                        teamOneStats, teamOneStats.numIntraGrpPos, teamOneStats.numExtraGrpPos,
                        firstTeamPos + startingPos, teamOneStats.isIgpUntiedByHeadToHead,
                        teamOneStats.isIgpUntiedRandomly, teamOneStats.isEgpUntiedRandomly, isFpUntiedRandomly = true
                    ),
                    Standing(
                        teamTwoStats, teamTwoStats.numIntraGrpPos, teamTwoStats.numExtraGrpPos,
                        secondTeamPos + startingPos, teamTwoStats.isIgpUntiedByHeadToHead,
                        teamTwoStats.isIgpUntiedRandomly, teamTwoStats.isEgpUntiedRandomly, isFpUntiedRandomly = true
                    )
                )
            }
        }

        data class Seeds(val seed: Int, val otherSeed: Int, val level: Int, var match: Match?)

        /**
         * Builds the funneling tree from the groups standings and finals matches.  The algorithm accepts an empty set
         * of finals matches - or a partially known set of them - to cater for building the tree before all the finals
         * matches are played.
         */
        fun buildFunnelingTree(groupStandings: Set<Standing>, finalsMatches: Set<Match> = emptySet()): Tree<Seeds> {
            val championship = groupStandings.first().championship

            // obtain qualified teams
            val qualifiedTeams: List<Standing> = groupStandings
                .filter { it.numExtraGrpPos!! <= championship.numQualif }

            // build the tree (it will have matches only on the leaves by this point)
            val numLevels = (ln(championship.numQualif.toDouble()) / ln(2.0)).toInt()
            fun insertNode(level: Int, seed: Int): Tree<Seeds> {
                val otherSeed = 2.0.pow(level.toDouble()).toInt() - seed + 1
                return if (level == numLevels) {
                    // there must be a match between qualified teams (this is the level right after the group stage)
                    val seedTeam = qualifiedTeams.find { it.numExtraGrpPos == seed }?.team
                    val otherSeedTeam = qualifiedTeams.find { it.numExtraGrpPos == otherSeed }?.team
                    val theMatch = finalsMatches
                        .find { it.teamA == seedTeam && it.teamB == otherSeedTeam }
                    Tree.Leaf(Seeds(seed, otherSeed, level, theMatch))
                } else
                    Tree.Branch(
                        Seeds(seed, otherSeed, level, null),
                        insertNode(level + 1, seed),
                        insertNode(level + 1, otherSeed)
                    )
            }
            val tree = insertNode(1, 1)

            // traverse the tree to set all remaining matches (needs to be done after building the tree as opposed to on
            // the same passage, because we only know about each match winner and loser by navigating from the leaves),
            // if they have already been played
            fun setTreeRootFinalMatch(tree: Tree<Seeds>): Unit =
                when (tree) {
                    is Tree.Branch -> {
                        val branch = tree
                        setTreeRootFinalMatch(branch.left)
                        setTreeRootFinalMatch(branch.right)
                        val leftWinner = getFromSubtreeRootMatch(branch.left, Match::winner)
                        val rightWinner = getFromSubtreeRootMatch(branch.right, Match::winner)
                        val finalMatch = finalsMatches.find { it.teamA == leftWinner && it.teamB == rightWinner }
                        branch.value.match = finalMatch  // match is mutable
                    }
                    is Tree.Leaf -> {
                        // nothing to do as the leaf match should already be defined during tree build
                    }
                }

            setTreeRootFinalMatch(tree)
            return tree
        }

        private fun findThirdPlacePlayoff(funnelingTree: Tree<Seeds>, finalsMatches: Set<Match>): Match? =
            when (funnelingTree) {
                is Tree.Branch -> {
                    val leftLoser = getFromSubtreeRootMatch(funnelingTree.left, Match::loser)
                    val rightLoser = getFromSubtreeRootMatch(funnelingTree.right, Match::loser)
                    finalsMatches.find { it.teamA == leftLoser && it.teamB == rightLoser }
                }
                is Tree.Leaf -> null
            }

        private fun getFromSubtreeRootMatch(tree: Tree<Seeds>?, whatToGet: (Match) -> Team?): Team? =
            when (tree) {
                is Tree.Branch -> if (tree.value.match != null) whatToGet(tree.value.match!!) else null
                is Tree.Leaf -> if (tree.value.match != null) whatToGet(tree.value.match!!) else null
                null -> null
            }

        private fun reduceInts(int1: Int?, int2: Int?): Int? {
            val sum = (int1 ?: 0) + (int2 ?: 0)
            return if (sum != 0) sum else null
        }

    }
}