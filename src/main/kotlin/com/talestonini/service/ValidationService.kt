package com.talestonini.service

class ValidationService {

    companion object {

        /**
         * Validate that matches are of the same championship and group.
         */
        fun validateGroupMatches(matches: Set<Match>) {
            assert(matches.size == Constants.NUM_MATCHES_PER_GROUP)
            // all matches are of the same championship
            assert(matches.map { it.championship.id }.distinct().size == 1)
            // all matches are of the same group
            assert(matches.map { it.type }.distinct().size == 1)
            assert(matches.first().type.code.lowercase().startsWith("g"))
        }

        /**
         * Validate that standings are of the same championship.
         */
        fun validateGroupStandings(standings: Set<Standing>) {
            val championship = standings.first().championship
            assert(standings.size == championship.numTeams)
            // all standings are of the same championship
            assert(standings.map { it.championship.id }.distinct().size == 1)
        }

    }

}