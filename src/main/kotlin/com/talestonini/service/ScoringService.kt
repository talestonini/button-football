package com.talestonini.service

import com.talestonini.model.ScoringEntity
import com.talestonini.model.ScoringsTable
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

data class Scoring(val id: Int?, val championshipType: ChampionshipType, val numPos: Int, val numPoints: Int) {
    // constructor without id field
    constructor(championshipType: ChampionshipType, numPos: Int, numPoints: Int) :
            this(null, championshipType, numPos, numPoints)
}

@Serializable
data class ScoringApiView(val championshipType: String, val numPos: Int, val numPoints: Int)

class ScoringService(database: Database) : BaseService() {

    companion object {
        fun toScoring(scoringEntity: ScoringEntity): Scoring =
            Scoring(
                ChampionshipTypeService.toChampionshipType(scoringEntity.championshipType),
                scoringEntity.numPos,
                scoringEntity.numPoints
            )

        fun toScoringApiView(scoringEntity: ScoringEntity): ScoringApiView =
            ScoringApiView(
                scoringEntity.championshipType.description,
                scoringEntity.numPos,
                scoringEntity.numPoints
            )
    }

    suspend fun read(codChampionshipType: String): List<ScoringApiView?> {
        return dbQuery {
            ScoringEntity.find {
                ScoringsTable.codChampionshipType eq codChampionshipType
            }
                .sortedWith(
                    compareBy(ScoringEntity::numPos)
                )
                .map { toScoringApiView(it) }
        }
    }

}