package com.talestonini.service

import com.talestonini.model.ChampionshipType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExpChampionshipType(val id: Int, val code: String, val description: String, val numEditions: Int,
                               val logoImgFile: String)

class ChampionshipTypeService(database: Database) : BaseService() {
    suspend fun read(id: Int): ExpChampionshipType {
        return dbQuery {
            toExpChampionshipType(ChampionshipType[id])
        }
    }

    suspend fun read(codTeamType: String?): List<ExpChampionshipType?> {
        return dbQuery {
            ChampionshipType.all()
                .filter { if (codTeamType != null) it.teamType.code == codTeamType else true }
                .sortedBy { it.listOrder }
                .map { toExpChampionshipType(it) }
        }
    }

    companion object {
        fun toExpChampionshipType(championshipType: ChampionshipType): ExpChampionshipType =
            ExpChampionshipType(
                championshipType.id.value,
                championshipType.code,
                championshipType.description,
                championshipType.numEditions,
                championshipType.logoImgFile
            )
    }
}