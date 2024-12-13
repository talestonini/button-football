package com.talestonini.service

import com.talestonini.model.TeamType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedTeamType(val id: Int, val code: String, val description: String)

class TeamTypeService(database: Database) : BaseService() {
    suspend fun read(code: String?): List<ExposedTeamType?> {
        return dbQuery {
            TeamType.all()
                .filter { if (code != null) it.code == code else true }
                .map { teamTypeMapper(it) }
        }
    }

    private fun teamTypeMapper(teamType: TeamType): ExposedTeamType =
        ExposedTeamType(
            teamType.id.value,
            teamType.code,
            teamType.description
        )
}