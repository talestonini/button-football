package com.talestonini.service

class ManagementService {

    companion object {
        private data class PWDL(val points: Int, val wins: Int, val draws: Int, val losses: Int)

        /**
         * Obtains points, wins, draws and losses for a team at a match.  In a single match, obviously, the number of
         * wins, draws and losses can only be 1.
         */
        private fun pwdl(match: Match, team: Team): PWDL =
            when (match.winner()) {
                team -> PWDL(3, 1, 0, 0)
                null -> PWDL(1, 0, 1, 0)
                else -> PWDL(0, 0, 0, 1)
            }

        fun matchStandings(match: Match): Pair<Standing, Standing> {
            val teamAPwdl = pwdl(match, match.teamA)
            val teamAStanding = Standing(
                match.championship, match.teamA, match.type, null, null, null, teamAPwdl.points, 1,
                teamAPwdl.wins, teamAPwdl.draws, teamAPwdl.losses, match.numGoalsTeamA ?: 0, match.numGoalsTeamB ?: 0,
                (match.numGoalsTeamA ?: 0) - (match.numGoalsTeamB ?: 0)
            )

            val teamBPwdl = pwdl(match, match.teamB)
            val teamBStanding = Standing(
                match.championship, match.teamB, match.type, null, null, null, teamBPwdl.points, 1,
                teamBPwdl.wins, teamBPwdl.draws, teamBPwdl.losses, match.numGoalsTeamB ?: 0, match.numGoalsTeamA ?: 0,
                (match.numGoalsTeamB ?: 0) - (match.numGoalsTeamA ?: 0)
            )

            return Pair(teamAStanding, teamBStanding)
        }

        private fun isTiedByAllCriteria(st1: Standing, st2: Standing): Boolean =
            st1.numPoints == st2.numPoints &&
                    st1.numWins == st2.numWins &&
                    st1.numGoalsDiff == st2.numGoalsDiff &&
                    st1.numGoalsScored == st2.numGoalsScored

        fun groupStandings(matches: Set<Match>): List<Standing> {
            ValidationService.validateGroupMatches(matches)

            // get match standings for each match
            val standings: List<Standing> = matches.flatMap { m ->
                val ms = matchStandings(m)
                listOf(ms.first, ms.second)
            }

            // group by team and compute totals by reducing each team's standings
            val reducedStandings = standings.groupBy { it.team }
                .mapValues {
                    it.value.reduce { acc, st ->
                        Standing(
                            acc.championship,
                            acc.team,
                            acc.type,
                            null,
                            null,
                            null,
                            acc.numPoints + st.numPoints,
                            acc.numMatches + st.numMatches,
                            acc.numWins + st.numWins,
                            acc.numDraws + st.numDraws,
                            acc.numLosses + st.numLosses,
                            acc.numGoalsScored + st.numGoalsScored,
                            acc.numGoalsConceded + st.numGoalsConceded,
                            acc.numGoalsDiff + st.numGoalsDiff
                        )
                    }
                }
                .map { it.value }

            // sort and detect possible ties by all criteria
            return reducedStandings.sortedWith(
                compareBy(
                    Standing::numPoints,
                    Standing::numWins,
                    Standing::numGoalsDiff,
                    Standing::numGoalsScored
                )
                    .reversed()
            )
                .withIndex()  // obtain a position number (1, 2, 3, 4)
                .zipWithNext()  // pair adjacent teams standings ((1,2), (2,3), (3,4))
                .map {
                    val isTied = isTiedByAllCriteria(it.first.value, it.second.value)
                    listOf(
                        Standing(it.first.value, it.first.index+1, isTied),
                        Standing(it.second.value, it.second.index+1, isTied)
                    )
                }
                .flatten()  // standings in positions 2 and 3 will have duplicates, as per zipWithNext
                .groupBy { it.team }
                .mapValues {
                    it.value.reduce { acc, st ->
                        Standing(acc, acc.numIntraGrpPos, acc.isIgpTied || st.isIgpTied)  // de-dup
                    }
                }
                .map { it.value }  // discard the key created in groupBy and return
        }
    }

}