package com.talestonini.service.sorting

import com.talestonini.service.Constants
import com.talestonini.service.Standing
import com.talestonini.service.ValidationService.Companion.validateSetOfChampionshipGroupStandings
import com.talestonini.service.sorting.CommonSortingService.Companion.isTiedByAllCriteria
import kotlin.random.Random

class ExtraGroupSortingService {
    companion object {

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

    }
}