package com.talestonini.service

class ValidationService {

    companion object {

        fun validateSetOfSingleGroupMatches(matches: Set<Match>) {
            assert(matches.size == Constants.NUM_MATCHES_PER_GROUP)
            // all matches are of the same championship
            assert(matches.map { it.championship.id }.distinct().size == 1)
            // all matches are of the same group
            assert(matches.map { it.type }.distinct().size == 1)
            assert(matches.first().type.code.lowercase().startsWith("g"))
        }

        fun validateSetOfChampionshipFinalsMatches(matches: Set<Match>) {
            val championship = matches.first().championship
            // the number of finals matches is the same as the number of teams qualified to the finals stage
            assert(matches.size == championship.numQualif)
            // all matches are of the same championship
            assert(matches.map { it.championship.id }.distinct().size == 1)
            // all matches are not of the groups stage
            assert(matches.all { !it.type.code.lowercase().startsWith("g") })
        }

        fun validateSetOfChampionshipGroupStandings(standings: Set<Standing>) {
            val championship = standings.first().championship
            // there is one group standing per team in the championship
            assert(standings.size == championship.numTeams)
            // all standings are of the same championship
            assert(standings.map { it.championship.id }.distinct().size == 1)
            // all standings are of the groups stage
            assert(standings.all { it.type.code.lowercase().startsWith("g")} )
        }

    }

}