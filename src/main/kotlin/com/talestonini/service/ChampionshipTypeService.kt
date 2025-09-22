package com.talestonini.service

import com.talestonini.model.ChampionshipTypeEntity
import kotlinx.serialization.Serializable

data class ChampionshipType(
    val id: Int?, val code: String, val description: String, val numEditions: Int, val logoImgFile: String,
) {
    constructor(code: String, description: String, numEditions: Int, logoImgFile: String) :
            this(null, code, description, numEditions, logoImgFile)
}

@Serializable
data class ChampionshipTypeApiView(
    val id: Int, val code: String, val description: String, val numEditions: Int, val logoImgFile: String,
)

class ChampionshipTypeService() : BaseService() {

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
                .filter { if (codTeamType != null) it.teamType.code == codTeamType else true }
                .sortedBy { it.listOrder }
                .map { toChampionshipTypeApiView(it) }
        }
    }

}