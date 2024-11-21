package com.talestonini

import com.talestonini.model.Teams
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

@Serializable
data class ExposedTeam(val id: Int, val name: String, val codType: String, val fullName: String, val foundation: String,
    val city: String, val codCountry: String, val logoImgFile: String)

class TeamService(database: Database) : BaseService() {
    suspend fun readByName(name: String): ExposedTeam? {
        return dbQuery {
            Teams.selectAll()
                .where { Teams.name eq name }
                .map { teamMapper(it) }
                .singleOrNull()
        }
    }

    private fun teamMapper(teamRow: ResultRow): ExposedTeam =
        ExposedTeam(
            teamRow[Teams.id].value,
            teamRow[Teams.name],
            teamRow[Teams.codType],
            teamRow[Teams.fullName],
            teamRow[Teams.foundation],
            teamRow[Teams.city],
            teamRow[Teams.codCountry],
            teamRow[Teams.logoImgFile]
        )
}