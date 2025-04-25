package com.talestonini.data

import com.talestonini.BaseTest
import com.talestonini.model.Match
import com.talestonini.service.MatchService
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class MatchesTest : BaseTest() {

    @Test
    fun `matches must have valid scores`() = testApplication {
        val matches = fromDb {
            Match.all().toList()
        }
        transaction {
            matches.forEach {
                assert(MatchService.toExposedMatch(it).isValidScores())
            }
        }
    }

}