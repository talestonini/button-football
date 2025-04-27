package com.talestonini.service

import com.talestonini.model.StandingEntity
import com.talestonini.model.StandingsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

data class Standing(
    val id: Int?, val championship: Championship, val team: Team, val type: MatchType, val numIntraGrpPos: Int?,
    val numExtraGrpPos: Int?, val numFinalPos: Int?, val numPoints: Int, val numMatches: Int, val numWins: Int,
    val numDraws: Int, val numLosses: Int, val numGoalsScored: Int, val numGoalsConceded: Int, val numGoalsDiff: Int,
    val isIgpTied: Boolean = false
) {
    constructor(
        championship: Championship, team: Team, type: MatchType, numIntraGrpPos: Int?, numExtraGrpPos: Int?,
        numFinalPos: Int?, numPoints: Int, numMatches: Int, numWins: Int, numDraws: Int, numLosses: Int,
        numGoalsScored: Int, numGoalsConceded: Int, numGoalsDiff: Int, isIgpTied: Boolean = false
    ) : this(
        null, championship, team, type, numIntraGrpPos, numExtraGrpPos, numFinalPos, numPoints, numMatches, numWins,
        numDraws, numLosses, numGoalsScored, numGoalsConceded, numGoalsDiff, isIgpTied
    )

    constructor(toClone: Standing, numIntraGrpPos: Int?, isIgpTied: Boolean) : this(
        toClone.id, toClone.championship, toClone.team, toClone.type, numIntraGrpPos, toClone.numExtraGrpPos,
        toClone.numFinalPos, toClone.numPoints, toClone.numMatches, toClone.numWins, toClone.numDraws,
        toClone.numLosses, toClone.numGoalsScored, toClone.numGoalsConceded, toClone.numGoalsDiff, isIgpTied
    )
}

@Serializable
data class StandingApiView(
    val id: Int, val championship: String, val edition: Int, val team: String, val teamLogoImgFile: String,
    val type: String, val numIntraGrpPos: Int?, val numExtraGrpPos: Int?, val numFinalPos: Int?, val numPoints: Int,
    val numMatches: Int, val numWins: Int, val numDraws: Int, val numLosses: Int, val numGoalsScored: Int,
    val numGoalsConceded: Int, val numGoalsDiff: Int,
)

class StandingService(database: Database) : BaseService() {

    companion object {
        fun toStanding(standingEntity: StandingEntity): Standing =
            Standing(
                ChampionshipService.toChampionship(standingEntity.championship),
                TeamService.toTeam(standingEntity.team),
                MatchTypeService.toMatchType(standingEntity.matchType),
                standingEntity?.numIntraGrpPos,
                standingEntity?.numExtraGrpPos,
                standingEntity?.numFinalPos,
                standingEntity.numPoints,
                standingEntity.numMatches,
                standingEntity.numWins,
                standingEntity.numDraws,
                standingEntity.numLosses,
                standingEntity.numGoalsScored,
                standingEntity.numGoalsConceded,
                standingEntity.numGoalsDiff
            )

        fun toStandingApiView(standingEntity: StandingEntity): StandingApiView =
            StandingApiView(
                standingEntity.id.value,
                standingEntity.championship.type.description,
                standingEntity.championship.numEdition,
                standingEntity.team.name,
                standingEntity.team.logoImgFile,
                standingEntity.matchType.description,
                standingEntity?.numIntraGrpPos,
                standingEntity?.numExtraGrpPos,
                standingEntity?.numFinalPos,
                standingEntity.numPoints,
                standingEntity.numMatches,
                standingEntity.numWins,
                standingEntity.numDraws,
                standingEntity.numLosses,
                standingEntity.numGoalsScored,
                standingEntity.numGoalsConceded,
                standingEntity.numGoalsDiff
            )
    }

    suspend fun read(championshipId: Int, matchTypes: List<String>? = emptyList()): List<StandingApiView?> {
        return dbQuery {
            StandingEntity.find { StandingsTable.idChampionship eq championshipId }
                .filter {
                    if (matchTypes?.isNotEmpty() == true) matchTypes.contains(it.matchType.description) else true
                }
                .sortedWith(
                    compareBy(
                        StandingEntity::numFinalPos,
                        StandingEntity::numExtraGrpPos,
                        StandingEntity::numIntraGrpPos
                    )
                )
                .map { toStandingApiView(it) }
        }
    }

}