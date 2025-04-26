package com.talestonini.service

import com.talestonini.model.ChampionshipTypeEntity
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

data class ChampionshipType(val code: String, val description: String, val numEditions: Int, val logoImgFile: String)

@Serializable
data class ChampionshipTypeApiView(val id: Int, val code: String, val description: String, val numEditions: Int,
                                   val logoImgFile: String)

class ChampionshipTypeService(database: Database) : BaseService() {

    companion object {
        fun toChampionshipType(championshipTypeEntity: ChampionshipTypeEntity): ChampionshipType =
            ChampionshipType(
                championshipTypeEntity.code,
                championshipTypeEntity.description,
                championshipTypeEntity.numEditions,
                championshipTypeEntity.logoImgFile
            )

        fun toChampionshipTypeApiView(championshipTypeEntity: ChampionshipTypeEntity): ChampionshipTypeApiView =
            ChampionshipTypeApiView(
                championshipTypeEntity.id.value,
                championshipTypeEntity.code,
                championshipTypeEntity.description,
                championshipTypeEntity.numEditions,
                championshipTypeEntity.logoImgFile
            )
    }

    suspend fun read(id: Int): ChampionshipTypeApiView {
        return dbQuery {
            toChampionshipTypeApiView(ChampionshipTypeEntity[id])
        }
    }

    suspend fun read(codTeamType: String?): List<ChampionshipTypeApiView?> {
        return dbQuery {
            ChampionshipTypeEntity.all()
                .filter { if (codTeamType != null) it.teamTypeEntity.code == codTeamType else true }
                .sortedBy { it.listOrder }
                .map { toChampionshipTypeApiView(it) }
        }
    }

}