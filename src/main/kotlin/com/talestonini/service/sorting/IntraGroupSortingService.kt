package com.talestonini.service.sorting

import com.talestonini.service.Match
import com.talestonini.service.Standing
import com.talestonini.service.Team
import com.talestonini.service.ValidationService.Companion.validateSetOfSingleGroupMatches
import com.talestonini.service.sorting.CommonSortingService.Companion.isTiedByAllCriteria
import com.talestonini.service.sorting.CommonSortingService.Companion.matchStandings
import kotlin.random.Random

class IntraGroupSortingService {
    companion object {

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

    }
}