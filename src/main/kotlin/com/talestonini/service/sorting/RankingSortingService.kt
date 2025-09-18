package com.talestonini.service.sorting

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
        fun processRanking(finalStandings: Set<Standing>): Set<Ranking> {
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

            return emptySet()
        }

    }
}