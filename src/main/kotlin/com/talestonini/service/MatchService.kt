package com.talestonini.service

import com.talestonini.model.Match
import com.talestonini.model.MatchesTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedMatch(val id: Int, val championship: String, val numEdition: Int, val type: String, val teamA: String,
                        val teamB: String, val teamALogoImgFile: String, val teamBLogoImgFile: String,
                        val numGoalsTeamA: Int?, val numGoalsTeamB: Int?, val numGoalsExtraA: Int?,
                        val numGoalsExtraB: Int?, val numGoalsPntA: Int?, val numGoalsPntB: Int?) {

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

    fun isGroupStageMatch(): Boolean = type.lowercase().startsWith("g")
    fun isFinalsMatch(): Boolean = !isGroupStageMatch()

    fun winner(): String? {
        assert(isValidScores())
        return if ("${numGoalsPntA ?: 0}${numGoalsExtraA ?: 0}${numGoalsTeamA ?: 0}".toInt() >
            "${numGoalsPntB ?: 0}${numGoalsExtraB ?: 0}${numGoalsTeamB ?: 0}".toInt()
        ) this.teamA
        else if ("${numGoalsPntA ?: 0}${numGoalsExtraA ?: 0}${numGoalsTeamA ?: 0}".toInt() <
            "${numGoalsPntB ?: 0}${numGoalsExtraB ?: 0}${numGoalsTeamB ?: 0}".toInt()
        ) this.teamB
        else null
    }

    fun looser(): String? =
        when (winner()) {
            teamA -> teamB
            teamB -> teamA
            else -> null
        }
}

enum class MatchState {
    PLAYED, UNPLAYED
}

class MatchService(database: Database) : BaseService() {
    suspend fun read(championshipId: Int, codMatchType: List<String>?): List<ExposedMatch?> {
        return dbQuery {
            Match.find { MatchesTable.idChampionship eq championshipId }
                .filter { if (codMatchType?.isNotEmpty() == true) codMatchType.contains(it.type.code) else true }
                .sortedBy { it.id.value }
                .map { toExposedMatch(it) }
        }
    }

    companion object {
        fun toExposedMatch(match: Match): ExposedMatch =
            ExposedMatch(
                match.id.value,
                match.championship.type.description,
                match.championship.numEdition,
                match.type.description,
                match.teamA.name,
                match.teamB.name,
                match.teamA.logoImgFile,
                match.teamB.logoImgFile,
                match?.numGoalsTeamA,
                match?.numGoalsTeamB,
                match?.numGoalsExtraA,
                match?.numGoalsExtraB,
                match?.numGoalsPntA,
                match?.numGoalsPntB
            )
    }
}