package com.talestonini.service

class ValidationService {

    companion object {
        fun validateGroupMatches(matches: Set<Match>) {
            assert(matches.size == Constants.NUM_MATCHES_PER_GROUP)
            assert(matches.map { it.championship.id }.distinct().size == 1)  // all matches are of the same championship
            assert(matches.map { it.type }.distinct().size == 1)  // all matches are of the same group
            assert(matches.first().type.code.lowercase().startsWith("g"))
        }
    }

}