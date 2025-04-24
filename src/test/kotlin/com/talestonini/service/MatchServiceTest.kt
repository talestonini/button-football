package com.talestonini.service

import com.talestonini.model.MatchType
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MatchServiceTest {

    private fun initDatabase() =
        Database.connect(
            url = "jdbc:h2:./h2/db/buttonfootball",
            driver = "org.h2.Driver",
            user = "sa",
            password = "buttonfootball"
        )

    private fun <T> wrapArbitrary(fn: () -> Arbitrary<T>): Arbitrary<T> {
        initDatabase()
        var res: Arbitrary<T> = Arbitraries.of()
        transaction {
            res = fn()
        }
        return res
    }

    @Provide
    fun matchTypes(): Arbitrary<String> =
        wrapArbitrary {
            Arbitraries.of(MatchType.all().map { it.description })
        }

    @Provide
    fun groupStageMatchTypes(): Arbitrary<String> =
        wrapArbitrary {
            Arbitraries.of(MatchType.all().filter { it.code.lowercase().startsWith("g") }.map { it.description })
        }

    @Provide
    fun finalsMatchTypes(): Arbitrary<String> =
        wrapArbitrary {
            Arbitraries.of(MatchType.all().filter { !it.code.lowercase().startsWith("g") }.map { it.description })
        }

    @Provide
    fun scores(): Arbitrary<Int> =
        Int.any().greaterOrEqual(0).lessOrEqual(10)

    @Property
    fun `an unplayed match has always valid scores`(@ForAll("matchTypes") matchType: String) {
        val match = ExposedMatch(0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", null, null, null, null,
            null, null)
        assertEquals(MatchState.UNPLAYED, match.matchState())
        assert(match.isValidScores())
    }

    @Property
    fun `a finals match that gets decided by the full time has always a winner and a looser`(
        @ForAll("finalsMatchTypes") matchType: String,
        @ForAll("scores") numGoalsTeamA: Int,
        @ForAll("scores") numGoalsTeamB: Int
    ) {
        val match = ExposedMatch(0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", numGoalsTeamA,
            numGoalsTeamB, null, null, null, null)
        if (match.isValidScores()) {
            assertNotNull(match.winner())
            assertNotNull(match.looser())
        } else {
            assertEquals(numGoalsTeamA, numGoalsTeamB)
        }
    }

    @Property
    fun `a finals match that gets decided by the extra time has always a winner and a looser`(
        @ForAll("finalsMatchTypes") matchType: String,
        @ForAll("scores") numGoalsFullTime: Int,
        @ForAll("scores") numGoalsExtraA: Int,
        @ForAll("scores") numGoalsExtraB: Int,
    ) {
        val match = ExposedMatch(0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", numGoalsFullTime,
            numGoalsFullTime, numGoalsExtraA, numGoalsExtraB, null, null)
        if (match.isValidScores()) {
            assertNotNull(match.winner())
            assertNotNull(match.looser())
        } else {
            assertEquals(numGoalsExtraA, numGoalsExtraB)
        }
    }

    @Property
    fun `a finals match that gets decided by penalty shootouts has always a winner and a looser`(
        @ForAll("finalsMatchTypes") matchType: String,
        @ForAll("scores") numGoalsFullTime: Int,
        @ForAll("scores") numGoalsExtraTime: Int,
        @ForAll("scores") numGoalsPntA: Int,
        @ForAll("scores") numGoalsPntB: Int
    ) {
        val match = ExposedMatch(0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", numGoalsFullTime,
            numGoalsFullTime, numGoalsExtraTime, numGoalsExtraTime, numGoalsPntA, numGoalsPntB)
        if (match.isValidScores()) {
            assertNotNull(match.winner())
            assertNotNull(match.looser())
        } else {
            assertEquals(numGoalsPntA, numGoalsPntB)
        }
    }

}