package com.talestonini.service

import com.talestonini.model.ChampionshipType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedChampionshipType(val id: Int, val code: String, val description: String, val numEditions: Int,
                                   val logoImgFile: String)

class ChampionshipTypeService(database: Database) : BaseService() {
    suspend fun read(id: Int): ExposedChampionshipType {
        return dbQuery {
            championshipTypeMapper(ChampionshipType[id])
        }
    }

    suspend fun read(codTeamType: String?): List<ExposedChampionshipType?> {
        return dbQuery {
            ChampionshipType.all()
                .filter { if (codTeamType != null) it.teamType.code == codTeamType else true }
                .sortedBy { it.listOrder }
                .map { championshipTypeMapper(it) }
        }
    }

    private fun championshipTypeMapper(championshipType: ChampionshipType): ExposedChampionshipType =
        ExposedChampionshipType(
            championshipType.id.value,
            championshipType.code,
            championshipType.description,
            championshipType.numEditions,
            championshipType.logoImgFile
        )
}