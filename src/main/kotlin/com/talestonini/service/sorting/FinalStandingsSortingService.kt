package com.talestonini.service.sorting

import com.talestonini.datastructure.Tree
import com.talestonini.service.Match
import com.talestonini.service.MatchState
import com.talestonini.service.Standing
import com.talestonini.service.Team
import com.talestonini.service.ValidationService.Companion.validateSetOfChampionshipFinalsMatches
import com.talestonini.service.ValidationService.Companion.validateSetOfChampionshipGroupStandings
import com.talestonini.service.sorting.CommonSortingService.Companion.matchStandings
import kotlin.collections.zipWithNext
import kotlin.math.ln
import kotlin.math.pow
import kotlin.random.Random

class FinalStandingsSortingService {
    companion object {

        private fun isTiedByAllCriteria(st1: MatchWithCampaignStanding, st2: MatchWithCampaignStanding): Boolean =
            st1.matchStanding.numPoints() == st2.matchStanding.numPoints() &&
                    st1.matchStanding.numGoalsDiff() == st2.matchStanding.numGoalsDiff() &&
                    st1.matchStanding.numGoalsScored == st2.matchStanding.numGoalsScored &&
                    st1.campaignStanding.numPoints() == st2.campaignStanding.numPoints() &&
                    st1.campaignStanding.numWins == st2.campaignStanding.numWins &&
                    st1.campaignStanding.numGoalsDiff() == st2.campaignStanding.numGoalsDiff() &&
                    st1.campaignStanding.numGoalsScored == st2.campaignStanding.numGoalsScored

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