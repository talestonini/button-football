package com.talestonini.service.sorting

import com.talestonini.service.Match
import com.talestonini.service.Standing
import com.talestonini.service.Team

class CommonSortingService {
    companion object {

        fun matchStandings(match: Match): Pair<Standing, Standing> {
            /**
             * Obtains points, wins, draws and losses for a team at a match.  In a single match, obviously, the number
             * of wins, draws and losses can only be 1.
             */
            data class WDL(val wins: Int, val draws: Int, val losses: Int)
            fun winsDrawsLosses(match: Match, team: Team): WDL =
                when (match.winner(isIncludeExtraTimeAndPenaltyShootouts = false)) {
                    team -> WDL(1, 0, 0)
                    null -> WDL(0, 1, 0)
                    else -> WDL(0, 0, 1)
                }

            val teamAWdl = winsDrawsLosses(match, match.teamA)
            val teamAStanding = Standing(
                match.championship, match.teamA, match.type, null, null, null, teamAWdl.wins, teamAWdl.draws,
                teamAWdl.losses, match.numGoalsTeamA ?: 0, match.numGoalsTeamB ?: 0, isIgpUntiedByHeadToHead = false,
                isIgpUntiedRandomly = false, isEgpUntiedRandomly = false, isFpUntiedRandomly = false
            )

            val teamBWdl = winsDrawsLosses(match, match.teamB)
            val teamBStanding = Standing(
                match.championship, match.teamB, match.type, null, null, null, teamBWdl.wins, teamBWdl.draws,
                teamBWdl.losses, match.numGoalsTeamB ?: 0, match.numGoalsTeamA ?: 0, isIgpUntiedByHeadToHead = false,
                isIgpUntiedRandomly = false, isEgpUntiedRandomly = false, isFpUntiedRandomly = false
            )

            return Pair(teamAStanding, teamBStanding)
        }

        fun isTiedByAllCriteria(st1: Standing, st2: Standing): Boolean =
            st1.numPoints() == st2.numPoints() &&
                    st1.numWins == st2.numWins &&
                    st1.numGoalsDiff() == st2.numGoalsDiff() &&
                    st1.numGoalsScored == st2.numGoalsScored

    }
}