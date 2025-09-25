package com.talestonini.service

import com.talestonini.model.RankingEntity
import com.talestonini.model.RankingsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and

data class Ranking(
    val id: Int?, val championshipType: ChampionshipType, val team: Team, val numBestPos: Int, val numWorstPos: Int,
    val numAvgPos: Double, val numParticipations: Int, val numRankingPoints: Int, val numRankingPos: Int,
    val numPoints: Int, val numMatches: Int, val numWins: Int, val numDraws: Int, val numLosses: Int,
    val numGoalsScored: Int, val numGoalsConceded: Int, val numGoalsDiff: Int, val numChampionships: Int,
    val numUpToEdition: Int,
) {
    // constructor without id field
    constructor(
        championshipType: ChampionshipType, team: Team, numBestPos: Int, numWorstPos: Int, numAvgPos: Double,
        numParticipations: Int, numRankingPoints: Int, numRankingPos: Int, numPoints: Int, numMatches: Int,
        numWins: Int, numDraws: Int, numLosses: Int, numGoalsScored: Int, numGoalsConceded: Int, numGoalsDiff: Int,
        numChampionships: Int, numUpToEdition: Int,
    ) : this(
        null, championshipType, team, numBestPos, numWorstPos, numAvgPos, numParticipations, numRankingPoints,
        numRankingPos, numPoints, numMatches, numWins, numDraws, numLosses, numGoalsScored, numGoalsConceded,
        numGoalsDiff, numChampionships, numUpToEdition
    )

    constructor(
        toClone: Ranking, numRankingPos: Int,
    ) : this(
        toClone.championshipType, toClone.team, toClone.numBestPos, toClone.numWorstPos, toClone.numAvgPos,
        toClone.numParticipations, toClone.numRankingPoints, numRankingPos, toClone.numPoints, toClone.numMatches,
        toClone.numWins, toClone.numDraws, toClone.numLosses, toClone.numGoalsScored, toClone.numGoalsConceded,
        toClone.numGoalsDiff, toClone.numChampionships, toClone.numUpToEdition
    )

    constructor(
        toClone: Ranking, numAvgPos: Double,
    ) : this(
        toClone.championshipType, toClone.team, toClone.numBestPos, toClone.numWorstPos, numAvgPos,
        toClone.numParticipations, toClone.numRankingPoints, toClone.numRankingPos, toClone.numPoints,
        toClone.numMatches, toClone.numWins, toClone.numDraws, toClone.numLosses, toClone.numGoalsScored,
        toClone.numGoalsConceded, toClone.numGoalsDiff, toClone.numChampionships, toClone.numUpToEdition
    )

    override fun toString(): String {
        return "ch=${championshipType.code}, upToEd=${numUpToEdition}, team=${team.name}, " +
                "rankingPos=${numRankingPos}, rankingPoints=${numRankingPoints}, titles=${numChampionships}, " +
                "participations=${numParticipations}, agvPos=${numAvgPos}, points=${numPoints}, wins=${numWins}, " +
                "gd=${numGoalsDiff}, gs=${numGoalsScored}, best=${numBestPos}, worst=${numWorstPos}, " +
                "matches=${numMatches}, draws=${numDraws}, losses=${numLosses}, " +
                "gc=${numGoalsConceded}"
    }
}

@Serializable
data class RankingApiView(
    val id: Int, val championshipType: String, val team: String, val teamLogoImgFile: String, val numBestPos: Int,
    val numWorstPos: Int, val numAvgPos: Double, val numParticipations: Int, val numRankingPoints: Int,
    val numRankingPos: Int, val numPoints: Int, val numMatches: Int, val numWins: Int, val numDraws: Int,
    val numLosses: Int, val numGoalsScored: Int, val numGoalsConceded: Int, val numGoalsDiff: Int,
    val numChampionships: Int, val numUpToEdition: Int,
)

class RankingService() : BaseService() {

    companion object {
        fun toRanking(rankingEntity: RankingEntity): Ranking =
            Ranking(
                ChampionshipTypeService.toChampionshipType(rankingEntity.championshipType),
                TeamService.toTeam(rankingEntity.team),
                rankingEntity.numBestPos,
                rankingEntity.numWorstPos,
                rankingEntity.numAvgPos,
                rankingEntity.numParticipations,
                rankingEntity.numRankingPoints,
                rankingEntity.numRankingPos,
                rankingEntity.numPoints,
                rankingEntity.numMatches,
                rankingEntity.numWins,
                rankingEntity.numDraws,
                rankingEntity.numLosses,
                rankingEntity.numGoalsScored,
                rankingEntity.numGoalsConceded,
                rankingEntity.numGoalsDiff,
                rankingEntity.numChampionships,
                rankingEntity.numUpToEdition
            )

        fun toRankingApiView(rankingEntity: RankingEntity): RankingApiView =
            RankingApiView(
                rankingEntity.id.value,
                rankingEntity.championshipType.description,
                rankingEntity.team.name,
                rankingEntity.team.logoImgFile,
                rankingEntity.numBestPos,
                rankingEntity.numWorstPos,
                rankingEntity.numAvgPos,
                rankingEntity.numParticipations,
                rankingEntity.numRankingPoints,
                rankingEntity.numRankingPos,
                rankingEntity.numPoints,
                rankingEntity.numMatches,
                rankingEntity.numWins,
                rankingEntity.numDraws,
                rankingEntity.numLosses,
                rankingEntity.numGoalsScored,
                rankingEntity.numGoalsConceded,
                rankingEntity.numGoalsDiff,
                rankingEntity.numChampionships,
                rankingEntity.numUpToEdition
            )
    }

    suspend fun read(codChampionshipType: String, numUpToEdition: Int): List<RankingApiView?> {
        return dbQuery {
            RankingEntity.find {
                RankingsTable.codChampionshipType eq codChampionshipType and
                        (RankingsTable.numUpToEdition eq numUpToEdition)
            }
                .sortedWith(
                    compareBy(RankingEntity::numRankingPos)
                )
                .map { toRankingApiView(it) }
        }
    }

}
