package com.talestonini.data

import com.talestonini.model.Match
import com.talestonini.service.MatchService
import org.junit.jupiter.api.Test

class MatchesTest : BaseDataTest() {

    @Test
    fun `matches must have valid scores`() = dataTest {
        Match.all().forEach {
            assert(MatchService.toExpMatch(it).isValidScores())
        }
    }

}