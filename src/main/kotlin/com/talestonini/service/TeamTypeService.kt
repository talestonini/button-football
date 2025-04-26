package com.talestonini.service

import com.talestonini.model.TeamType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExpTeamType(val id: Int, val code: String, val description: String)

class TeamTypeService(database: Database) : BaseService() {
    suspend fun read(code: String?): List<ExpTeamType?> {
        return dbQuery {
            TeamType.all()
                .filter { if (code != null) it.code == code else true }
                .map { toExpTeamType(it) }
        }
    }

    companion object {
        fun toExpTeamType(teamType: TeamType): ExpTeamType =
            ExpTeamType(
                teamType.id.value,
                teamType.code,
                teamType.description
            )
    }
}