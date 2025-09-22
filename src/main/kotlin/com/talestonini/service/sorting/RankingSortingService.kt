package com.talestonini.service.sorting

import com.talestonini.service.ChampionshipService
import com.talestonini.service.Ranking
import com.talestonini.service.Standing

class RankingSortingService {
    companion object {

        /**
         * Processes the ranging of a championship type given the set of final standings to be computed.  For example,
         * if wanting to obtain the ranking at the end of the third edition of a championship type, then the set of
         * final standings must contain the final standings of the 3 first championships of that championship type.  In
         * that example, a team that participated in all 3 editions should have 3 entries in the set.
         */
        fun processRanking(finalStandings: Set<Standing>, scoring: Map<Int, Int>): Set<Ranking> {
            // all standings are of the same championship type
            val championshipType = finalStandings.first().championship.type
            finalStandings.all { it.championship.type == championshipType }
            // editions start in 1 and have no gaps
            val editions = finalStandings.map { it.championship.numEdition }.distinct().sorted()
            assert(editions.min() == 1)
            val maxEdition = editions.max()
            assert(editions.size == maxEdition)
            // there are no missing standings
            for (i in 1..maxEdition) {
                val iStandings = finalStandings.filter { it.championship.numEdition == i }
                assert(iStandings.size == iStandings.first().championship.numTeams)
            }
            // all standings are final
            finalStandings.all { it.type.code.lowercase() == "f1" }
            // all standings have a final position
            finalStandings.all { it.numFinalPos!! > 0 }

            val numQualifPerEdition: Map<Int, Int> = finalStandings.groupBy { it.championship.numEdition }
                .mapValues { ChampionshipService.calcNumQualif(it.value.count()) }

            // compute ranking entries
            val ranking: List<Ranking> = finalStandings.groupBy { it.team }
                .mapValues { teamStandings ->
                    val stCount = teamStandings.value.size
                    val initialRanking = Ranking(
                        championshipType, teamStandings.key, Int.MAX_VALUE, Int.MIN_VALUE, 0.0,
                        stCount, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, stCount
                    )
                    teamStandings.value.fold(initialRanking, { acc, st ->
                        val numRankingPoints =
                            if (st.numFinalPos!! <= numQualifPerEdition[st.championship.numEdition]!!)
                                scoring[st.numFinalPos]!!
                            else
                                0
                        Ranking(
                            acc.championshipType, acc.team,
                            if (st.numFinalPos < acc.numBestPos) st.numFinalPos else acc.numBestPos,
                            if (st.numFinalPos > acc.numWorstPos) st.numFinalPos else acc.numWorstPos,
                            acc.numAvgPos + (1.0 * st.numFinalPos / stCount), stCount,
                            acc.numRankingPoints + numRankingPoints, -1, acc.numPoints + st.numPoints(),
                            acc.numMatches + st.numMatches(), acc.numWins + st.numWins, acc.numDraws + st.numDraws,
                            acc.numLosses + st.numLosses, acc.numGoalsScored + st.numGoalsScored,
                            acc.numGoalsConceded + st.numGoalsConceded, acc.numGoalsDiff + st.numGoalsDiff(),
                            acc.numChampionships + (if (st.numFinalPos > 1) 0 else 1), maxEdition
                        )
                    })
                }
                .map { it.value }  // discard the key created in groupBy

            // sort and detect possible ties by all criteria
            return ranking.sortedWith(
                compareBy(Ranking::numRankingPoints, Ranking::numChampionships, Ranking::numParticipations)
                    .thenByDescending(Ranking::numAvgPos)
                    .thenBy(Ranking::numPoints)
                    .thenBy(Ranking::numWins)
                    .thenBy(Ranking::numGoalsDiff)
                    .thenBy(Ranking::numGoalsScored)
                    .reversed()
            )
                .withIndex()  // obtain a position number (0, 1, 2, 3)
                .map { Ranking(it.value, it.index + 1) }
                .toSet()
        }

    }
}