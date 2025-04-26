package com.talestonini.service

import com.talestonini.PropertyBasedTest
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MatchEntityServiceTest : PropertyBasedTest() {

    @Property
    fun `an unplayed match has always valid scores`(@ForAll("matchTypes") matchType: MatchType) {
        val match = Match(
            anyChampionship, matchType, anyTeam, anyOtherTeam, null, null, null, null, null, null
        )
        assertEquals(MatchState.UNPLAYED, match.matchState())
        assert(match.isValidScores())
    }

    @Property
    fun `a finals match that gets decided by the full time has always a winner and a looser`(
        @ForAll("finalsMatchTypes") matchType: MatchType,
        @ForAll("scores") numGoalsTeamA: Int,
        @ForAll("scores") numGoalsTeamB: Int,
    ) {
        val match = Match(
            anyChampionship, matchType, anyTeam, anyOtherTeam, numGoalsTeamA, numGoalsTeamB, null, null, null, null
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
        @ForAll("finalsMatchTypes") matchType: MatchType,
        @ForAll("scores") numGoalsFullTime: Int,
        @ForAll("scores") numGoalsExtraA: Int,
        @ForAll("scores") numGoalsExtraB: Int,
    ) {
        val match = Match(
            anyChampionship, matchType, anyTeam, anyOtherTeam, numGoalsFullTime, numGoalsFullTime, numGoalsExtraA,
            numGoalsExtraB, null, null
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
        @ForAll("finalsMatchTypes") matchType: MatchType,
        @ForAll("scores") numGoalsFullTime: Int,
        @ForAll("scores") numGoalsExtraTime: Int,
        @ForAll("scores") numGoalsPntA: Int,
        @ForAll("scores") numGoalsPntB: Int,
    ) {
        val match = Match(
            anyChampionship, matchType, anyTeam, anyOtherTeam, numGoalsFullTime, numGoalsFullTime, numGoalsExtraTime,
            numGoalsExtraTime, numGoalsPntA, numGoalsPntB
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