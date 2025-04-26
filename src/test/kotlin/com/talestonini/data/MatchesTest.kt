package com.talestonini.data

import com.talestonini.model.MatchEntity
import com.talestonini.service.MatchService
import org.junit.jupiter.api.Test

class MatchesTest : BaseDataTest() {

    @Test
    fun `matches must have valid scores`() = dataTest {
        MatchEntity.all().forEach {
            assert(MatchService.toMatch(it).isValidScores())
        }
    }

}