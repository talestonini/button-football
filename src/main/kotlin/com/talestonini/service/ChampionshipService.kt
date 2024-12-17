package com.talestonini.service

import com.talestonini.model.Championship
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedChampionship(val id: Int, val type: String, val teamType: String, val numEdition: Int,
                               val dtCreation: String, val dtEnd: String?, val numTeams: Int, val numQualif: Int,
                               val status: String)

class ChampionshipService(database: Database) : BaseService() {
    suspend fun read(id: Int): ExposedChampionship {
        return dbQuery {
            championshipMapper(Championship[id])
        }
    }

    suspend fun read(codChampionshipType: String?): List<ExposedChampionship?> {
        return dbQuery {
            Championship.all()
                .filter { if (codChampionshipType != null) it.type.code == codChampionshipType else true }
                .sortedBy { it.numEdition }
                .map { championshipMapper(it) }
        }
    }

    private fun championshipMapper(championship: Championship): ExposedChampionship =
        ExposedChampionship(
            championship.id.value,
            championship.type.description,
            championship.type.teamType.description,
            championship.numEdition,
            championship.dtCreation,
            championship?.dtEnd,
            championship.numTeams,
            championship.numQualif,
            championship.status.description
        )
}
