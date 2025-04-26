package com.talestonini.service

import com.talestonini.model.ChampionshipStatusEntity
import kotlinx.serialization.Serializable

data class ChampionshipStatus(val code: String, val description: String)

@Serializable
data class ChampionshipStatusApiView(val id: Int, val code: String, val description: String)

class ChampionshipStatusService {

    companion object {
        fun toChampionshipStatus(championshipStatusEntity: ChampionshipStatusEntity) =
            ChampionshipStatus(
                championshipStatusEntity.code,
                championshipStatusEntity.description
            )

        fun toChampionshipStatusApiView(championshipStatusEntity: ChampionshipStatusEntity) =
            ChampionshipStatusApiView(
                championshipStatusEntity.id.value,
                championshipStatusEntity.code,
                championshipStatusEntity.description
            )
    }

}