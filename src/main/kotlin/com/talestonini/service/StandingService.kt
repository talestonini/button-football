package com.talestonini.service

import com.talestonini.model.Standing
import com.talestonini.model.StandingsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedStanding(val id: Int, val championship: String, val team: String, val type: String,
                           val numIntraGrpPos: Int?, val numExtraGrpPos: Int?, val numFinalPos: Int?,
                           val numPoints: Int, val numMatches: Int, val numWins: Int, val numDraws: Int,
                           val numLosses: Int, val numGoalsScored: Int, val numGoalsConceded: Int,
                           val numGoalDiff: Int)

class StandingService(database: Database) : BaseService() {
    suspend fun read(championshipId: Int, matchTypes: List<String>?): List<ExposedStanding?> {
        return dbQuery {
            Standing.find { StandingsTable.idChampionship eq championshipId }
                .filter {
                    if (matchTypes?.isNotEmpty() == true) matchTypes.contains(it.matchType.description) else true
                }
                .sortedWith(compareBy(Standing::numFinalPos, Standing::numExtraGrpPos, Standing::numIntraGrpPos))
                .map { standingMapper(it) }
        }
    }

    private fun standingMapper(standing: Standing): ExposedStanding =
        ExposedStanding(
            standing.id.value,
            standing.championship.type.description,
            standing.team.name,
            standing.matchType.description,
            standing?.numIntraGrpPos,
            standing?.numExtraGrpPos,
            standing?.numFinalPos,
            standing.numPoints,
            standing.numMatches,
            standing.numWins,
            standing.numDraws,
            standing.numLosses,
            standing.numGoalsScored,
            standing.numGoalsConceded,
            standing.numGoalDiff
        )
}