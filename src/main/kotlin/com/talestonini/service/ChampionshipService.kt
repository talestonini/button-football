package com.talestonini.service

import com.talestonini.model.ChampionshipEntity
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

data class Championship(
    val id: Int?, val type: ChampionshipType, val numEdition: Int, val dtCreation: String, val dtEnd: String?,
    val numTeams: Int, val numQualif: Int, val status: ChampionshipStatus,
) {
    constructor(
        type: ChampionshipType, numEdition: Int, dtCreation: String, dtEnd: String?, numTeams: Int,
        numQualif: Int, status: ChampionshipStatus,
    ) : this(null, type, numEdition, dtCreation, dtEnd, numTeams, numQualif, status)
}

@Serializable
data class ChampionshipApiView(
    val id: Int, val type: String, val teamType: String, val numEdition: Int, val dtCreation: String,
    val dtEnd: String?, val numTeams: Int, val numQualif: Int, val status: String,
)

class ChampionshipService(database: Database) : BaseService() {

    companion object {
        fun toChampionship(championshipEntity: ChampionshipEntity): Championship =
            Championship(
                ChampionshipTypeService.toChampionshipType(championshipEntity.type),
                championshipEntity.numEdition,
                championshipEntity.dtCreation,
                championshipEntity?.dtEnd,
                championshipEntity.numTeams,
                championshipEntity.numQualif,
                ChampionshipStatusService.toChampionshipStatus(championshipEntity.status)
            )

        fun toChampionshipApiView(championshipEntity: ChampionshipEntity): ChampionshipApiView =
            ChampionshipApiView(
                championshipEntity.id.value,
                championshipEntity.type.description,
                championshipEntity.type.teamType.description,
                championshipEntity.numEdition,
                championshipEntity.dtCreation,
                championshipEntity?.dtEnd,
                championshipEntity.numTeams,
                championshipEntity.numQualif,
                championshipEntity.status.description
            )
    }

    suspend fun read(id: Int): ChampionshipApiView {
        return dbQuery {
            toChampionshipApiView(ChampionshipEntity[id])
        }
    }

    suspend fun read(codChampionshipType: String?): List<ChampionshipApiView?> {
        return dbQuery {
            ChampionshipEntity.all()
                .filter { if (codChampionshipType != null) it.type.code == codChampionshipType else true }
                .sortedBy { it.numEdition }
                .map { toChampionshipApiView(it) }
        }
    }

}