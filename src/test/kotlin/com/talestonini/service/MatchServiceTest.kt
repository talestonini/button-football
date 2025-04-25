package com.talestonini.service

import com.talestonini.PropertyBasedTest
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MatchServiceTest : PropertyBasedTest() {

    @Property
    fun `an unplayed match has always valid scores`(@ForAll("matchTypes") matchType: String) {
        val match = ExposedMatch(
            0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", null, null, null, null, null, null
        )
        assertEquals(MatchState.UNPLAYED, match.matchState())
        assert(match.isValidScores())
    }

    @Property
    fun `a finals match that gets decided by the full time has always a winner and a looser`(
        @ForAll("finalsMatchTypes") matchType: String,
        @ForAll("scores") numGoalsTeamA: Int,
        @ForAll("scores") numGoalsTeamB: Int,
    ) {
        val match = ExposedMatch(
            0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", numGoalsTeamA, numGoalsTeamB, null, null, null,
            null
        )
        if (match.isValidScores()) {
            assertEquals(MatchState.PLAYED, match.matchState())
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
        val match = ExposedMatch(
            0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", numGoalsFullTime, numGoalsFullTime,
            numGoalsExtraA, numGoalsExtraB, null, null
        )
        if (match.isValidScores()) {
            assertEquals(MatchState.PLAYED, match.matchState())
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
        @ForAll("scores") numGoalsPntB: Int,
    ) {
        val match = ExposedMatch(
            0, "aChampionship", 1, matchType, "teamA", "teamB", "", "", numGoalsFullTime, numGoalsFullTime,
            numGoalsExtraTime, numGoalsExtraTime, numGoalsPntA, numGoalsPntB
        )
        if (match.isValidScores()) {
            assertEquals(MatchState.PLAYED, match.matchState())
            assertNotNull(match.winner())
            assertNotNull(match.looser())
        } else {
            assertEquals(numGoalsPntA, numGoalsPntB)
        }
    }

}