package com.talestonini.service

import com.talestonini.model.Match
import com.talestonini.model.MatchesTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedMatch(val id: Int, val championship: String, val edition: Int, val type: String, val teamA: String,
                        val teamALogoImgFile: String, val teamB: String, val teamBLogoImgFile: String,
                        val numGoalsTeamA: Int, val numGoalsTeamB: Int, val numGoalsExtraA: Int?,
                        val numGoalsExtraB: Int?, val numGoalsPntA: Int?, val numGoalsPntB: Int?)

class MatchService(database: Database) : BaseService() {
    suspend fun read(championshipId: Int, codMatchType: String?): List<ExposedMatch?> {
        return dbQuery {
            Match.find { MatchesTable.idChampionship eq championshipId }
                .filter { if (codMatchType != null) it.type.code == codMatchType else true }
                .sortedBy { it.id.value }
                .map { matchMapper(it) }
        }
    }

    private fun matchMapper(match: Match): ExposedMatch =
        ExposedMatch(
            match.id.value,
            match.championship.type.description,
            match.championship.numEdition,
            match.type.description,
            match.teamA.name,
            match.teamA.logoImgFile,
            match.teamB.name,
            match.teamB.logoImgFile,
            match.numGoalsTeamA,
            match.numGoalsTeamB,
            match.numGoalsExtraA,
            match.numGoalsExtraB,
            match.numGoalsPntA,
            match.numGoalsPntB
        )
}