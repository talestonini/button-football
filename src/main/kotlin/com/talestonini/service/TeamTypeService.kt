package com.talestonini.service

import com.talestonini.model.TeamTypeEntity
import kotlinx.serialization.Serializable

data class TeamType(val id: Int?, val code: String, val description: String) {
    constructor(code: String, description: String) : this(null, code, description)
}

@Serializable
data class TeamTypeApiView(val id: Int, val code: String, val description: String)

class TeamTypeService() : BaseService() {

    companion object {
        fun toTeamType(teamTypeEntity: TeamTypeEntity): TeamType =
            TeamType(
                teamTypeEntity.code,
                teamTypeEntity.description
            )

        fun toTeamTypeApiView(teamTypeEntity: TeamTypeEntity): TeamTypeApiView =
            TeamTypeApiView(
                teamTypeEntity.id.value,
                teamTypeEntity.code,
                teamTypeEntity.description
            )
    }

    suspend fun read(code: String?): List<TeamTypeApiView?> {
        return dbQuery {
            TeamTypeEntity.all()
                .filter { if (code != null) it.code == code else true }
                .map { toTeamTypeApiView(it) }
        }
    }

}