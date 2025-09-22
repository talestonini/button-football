package com.talestonini.service

import com.talestonini.model.MatchEntity
import com.talestonini.model.MatchesTable
import kotlinx.serialization.Serializable

enum class MatchState {
    PLAYED, UNPLAYED
}

data class Match(
    val id: Int?, val championship: Championship, val type: MatchType, val teamA: Team, val teamB: Team,
    val numGoalsTeamA: Int?, val numGoalsTeamB: Int?, val numGoalsExtraA: Int?, val numGoalsExtraB: Int?,
    val numGoalsPntA: Int?, val numGoalsPntB: Int?,
) {

    constructor(
        championship: Championship, type: MatchType, teamA: Team, teamB: Team, numGoalsTeamA: Int?,
        numGoalsTeamB: Int?, numGoalsExtraA: Int?, numGoalsExtraB: Int?, numGoalsPntA: Int?,
        numGoalsPntB: Int?,
    ) : this(
        null, championship, type, teamA, teamB, numGoalsTeamA, numGoalsTeamB, numGoalsExtraA, numGoalsExtraB,
        numGoalsPntA, numGoalsPntB
    )

    private fun isFullTimeScoreNull(): Boolean = numGoalsTeamA == null && numGoalsTeamB == null
    private fun isExtraTimeScoreNull(): Boolean = numGoalsExtraA == null && numGoalsExtraB == null
    private fun isPenaltiesScoreNull(): Boolean = numGoalsPntA == null && numGoalsPntB == null
    private fun isAllScoresNull(): Boolean = isFullTimeScoreNull() && isExtraTimeScoreNull() && isPenaltiesScoreNull()

    fun isValidScores(): Boolean =
        // all the scores are null, so the match is not played yet, therefore the scores are valid
        if (isAllScoresNull())
            true
        // a group stage match only needs the full time score to not be null and the other scores to be null
        else if (isGroupStageMatch())
            !isFullTimeScoreNull() && isExtraTimeScoreNull() && isPenaltiesScoreNull()
        // a finals match with only the full time score must not be a draw
        else if (!isFullTimeScoreNull() && isExtraTimeScoreNull() && isPenaltiesScoreNull())
            numGoalsTeamA != numGoalsTeamB
        // a finals match with extra time score must be a draw in the full time and not be a draw in the extra time
        else if (!isFullTimeScoreNull() && !isExtraTimeScoreNull() && isPenaltiesScoreNull())
            numGoalsTeamA == numGoalsTeamB && numGoalsExtraA != numGoalsExtraB
        // a finals match with penalty shootouts score must be a draw in both the full and extra times and not be a draw
        // in the penalty shootouts
        else if (!isFullTimeScoreNull() && !isExtraTimeScoreNull() && !isPenaltiesScoreNull())
            numGoalsTeamA == numGoalsTeamB && numGoalsExtraA == numGoalsExtraB && numGoalsPntA != numGoalsPntB
        else
            false

    fun matchState(): MatchState {
        assert(isValidScores())
        return if (isAllScoresNull()) MatchState.UNPLAYED else MatchState.PLAYED
    }

    fun isGroupStageMatch(): Boolean = type.code.lowercase().startsWith("g")
    fun isFinalsMatch(): Boolean = !isGroupStageMatch()

    fun winner(isIncludeExtraTimeAndPenaltyShootouts: Boolean = true): Team? {
        assert(isValidScores())
        return if (isIncludeExtraTimeAndPenaltyShootouts) {
            if ("${numGoalsPntA ?: 0}${numGoalsExtraA ?: 0}${numGoalsTeamA ?: 0}".toInt() >
                "${numGoalsPntB ?: 0}${numGoalsExtraB ?: 0}${numGoalsTeamB ?: 0}".toInt()
            ) this.teamA
            else if ("${numGoalsPntA ?: 0}${numGoalsExtraA ?: 0}${numGoalsTeamA ?: 0}".toInt() <
                "${numGoalsPntB ?: 0}${numGoalsExtraB ?: 0}${numGoalsTeamB ?: 0}".toInt()
            ) this.teamB
            else null
        } else {
            if ((numGoalsTeamA ?: 0) > (numGoalsTeamB ?: 0)) this.teamA
            else if ((numGoalsTeamA ?: 0) < (numGoalsTeamB ?: 0)) this.teamB
            else null
        }
    }

    fun loser(isIncludeExtraTimeAndPenaltyShootouts: Boolean = true): Team? =
        when (winner(isIncludeExtraTimeAndPenaltyShootouts)) {
            teamA -> teamB
            teamB -> teamA
            else -> null
        }
}

@Serializable
data class MatchApiView(
    val id: Int, val championship: String, val numEdition: Int, val type: String, val teamA: String, val teamB: String,
    val teamALogoImgFile: String, val teamBLogoImgFile: String, val numGoalsTeamA: Int?, val numGoalsTeamB: Int?,
    val numGoalsExtraA: Int?, val numGoalsExtraB: Int?, val numGoalsPntA: Int?, val numGoalsPntB: Int?,
)

class MatchService() : BaseService() {

    companion object {
        fun toMatch(matchEntity: MatchEntity): Match =
            Match(
                ChampionshipService.toChampionship(matchEntity.championship),
                MatchTypeService.toMatchType(matchEntity.type),
                TeamService.toTeam(matchEntity.teamA),
                TeamService.toTeam(matchEntity.teamB),
                matchEntity?.numGoalsTeamA,
                matchEntity?.numGoalsTeamB,
                matchEntity?.numGoalsExtraA,
                matchEntity?.numGoalsExtraB,
                matchEntity?.numGoalsPntA,
                matchEntity?.numGoalsPntB
            )

        fun toMatchApiView(matchEntity: MatchEntity): MatchApiView =
            MatchApiView(
                matchEntity.id.value,
                matchEntity.championship.type.description,
                matchEntity.championship.numEdition,
                matchEntity.type.description,
                matchEntity.teamA.name,
                matchEntity.teamB.name,
                matchEntity.teamA.logoImgFile,
                matchEntity.teamB.logoImgFile,
                matchEntity?.numGoalsTeamA,
                matchEntity?.numGoalsTeamB,
                matchEntity?.numGoalsExtraA,
                matchEntity?.numGoalsExtraB,
                matchEntity?.numGoalsPntA,
                matchEntity?.numGoalsPntB
            )
    }

    suspend fun read(championshipId: Int, codMatchType: List<String>?): List<MatchApiView?> {
        return dbQuery {
            MatchEntity.find { MatchesTable.idChampionship eq championshipId }
                .filter { if (codMatchType?.isNotEmpty() == true) codMatchType.contains(it.type.code) else true }
                .sortedBy { it.id.value }
                .map { toMatchApiView(it) }
        }
    }

}