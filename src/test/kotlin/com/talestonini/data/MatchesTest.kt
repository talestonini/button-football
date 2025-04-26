package com.talestonini.data

import com.talestonini.model.Match
import com.talestonini.service.MatchService
import org.junit.jupiter.api.Test

class MatchesTest : BaseDataTest() {

    @Test
    fun `matches must have valid scores`() = dataTest {
        val matches = Match.all().toList()
        matches.forEach {
            assert(MatchService.toExposedMatch(it).isValidScores())
        }
    }

}