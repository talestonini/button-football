package com.talestonini.service

import kotlin.random.Random

class ManagementService {

    companion object {
        private data class WDL(val wins: Int, val draws: Int, val losses: Int)

        /**
         * Obtains points, wins, draws and losses for a team at a match.  In a single match, obviously, the number of
         * wins, draws and losses can only be 1.
         */
        private fun wdl(match: Match, team: Team): WDL =
            when (match.winner()) {
                team -> WDL(1, 0, 0)
                null -> WDL(0, 1, 0)
                else -> WDL(0, 0, 1)
            }

        fun matchStandings(match: Match): Pair<Standing, Standing> {
            val teamAWdl = wdl(match, match.teamA)
            val teamAStanding = Standing(
                match.championship, match.teamA, match.type, null, null, null, teamAWdl.wins, teamAWdl.draws,
                teamAWdl.losses, match.numGoalsTeamA ?: 0, match.numGoalsTeamB ?: 0, isIgpUntiedByHeadToHead = false,
                isIgpUntiedRandomly = false, isEgpUntiedRandomly = false
            )

            val teamBWdl = wdl(match, match.teamB)
            val teamBStanding = Standing(
                match.championship, match.teamB, match.type, null, null, null, teamBWdl.wins, teamBWdl.draws,
                teamBWdl.losses, match.numGoalsTeamB ?: 0, match.numGoalsTeamA ?: 0, isIgpUntiedByHeadToHead = false,
                isIgpUntiedRandomly = false, isEgpUntiedRandomly = false
            )

            return Pair(teamAStanding, teamBStanding)
        }

        fun isTiedByAllCriteria(st1: Standing, st2: Standing): Boolean =
            st1.numPoints() == st2.numPoints() &&
                    st1.numWins == st2.numWins &&
                    st1.numGoalsDiff() == st2.numGoalsDiff() &&
                    st1.numGoalsScored == st2.numGoalsScored

        /**
         * Processes intra-group standings for a single group of a championship, given the set of 6 matches of that
         * group.
         */
        fun processIntraGroupStandings(groupMatches: Set<Match>): Set<Standing> {
            ValidationService.validateGroupMatches(groupMatches)

            // get match standings for each match
            val standings: List<Standing> = groupMatches.flatMap { m ->
                val ms = matchStandings(m)
                listOf(ms.first, ms.second)
            }

            // group by team and compute totals by reducing each team's standings
            val reducedStandings = standings.groupBy { it.team }
                .mapValues {
                    it.value.reduce { acc, st ->
                        Standing(acc.championship, acc.team, acc.type, null, null, null,
                            acc.numWins + st.numWins,
                            acc.numDraws + st.numDraws,
                            acc.numLosses + st.numLosses,
                            acc.numGoalsScored + st.numGoalsScored,
                            acc.numGoalsConceded + st.numGoalsConceded,
                            isIgpUntiedByHeadToHead = false, isIgpUntiedRandomly = false, isEgpUntiedRandomly = false
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
                .flatMap { processPossiblyTiedIntraGroupStanding(it, groupMatches) }
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

        private fun processPossiblyTiedIntraGroupStanding(
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
                val firstTeamPos  = if (isFirstTeamToComeFirst) it.first.index else it.second.index
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
        fun processExtraGroupStandings(groupStageStandings: Set<Standing>): Set<Standing> {
            ValidationService.validateGroupStandings(groupStageStandings)

            fun sortAllOfSameIntraGroupPosition(standings: Set<Standing>, startingPos: Int): Set<Standing> {
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

            val numGroups = groupStageStandings.first().championship.numTeams/Constants.NUM_TEAMS_PER_GROUP
            return (1..Constants.NUM_TEAMS_PER_GROUP).map { igp ->
                sortAllOfSameIntraGroupPosition(
                    groupStageStandings.filter { it.numIntraGrpPos == igp }.toSet(),
                    (igp-1) * numGroups + 1
                )
            }.flatten().toSet()
        }

        private fun processPossiblyTiedExtraGroupStandings(
            it: Pair<IndexedValue<Standing>, IndexedValue<Standing>>,
            startingPos: Int
        ): List<Standing> =
            if (!isTiedByAllCriteria(it.first.value, it.second.value))
                listOf(
                    Standing(it.first.value, it.first.value.numIntraGrpPos, it.first.index + startingPos, null,
                        it.first.value.isIgpUntiedByHeadToHead, it.first.value.isIgpUntiedRandomly),
                    Standing(it.second.value, it.second.value.numIntraGrpPos, it.second.index + startingPos, null,
                        it.second.value.isIgpUntiedByHeadToHead, it.second.value.isIgpUntiedRandomly),
                )
            else {
                val isFirstTeamToComeFirst = Random.nextBoolean()
                val firstTeamPos  = if (isFirstTeamToComeFirst) it.first.index else it.second.index
                val secondTeamPos = if (isFirstTeamToComeFirst) it.second.index else it.first.index
                listOf(
                    Standing(it.first.value, it.first.value.numIntraGrpPos, firstTeamPos + startingPos, null,
                        it.first.value.isIgpUntiedByHeadToHead, it.first.value.isIgpUntiedRandomly,
                        isEgpUntiedRandomly = true),
                    Standing(it.second.value, it.second.value.numIntraGrpPos, secondTeamPos + startingPos, null,
                        it.second.value.isIgpUntiedByHeadToHead, it.second.value.isIgpUntiedRandomly,
                        isEgpUntiedRandomly = true)
                )
            }
    }

}