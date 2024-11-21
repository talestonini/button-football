package com.talestonini

import com.talestonini.model.Teams
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll

@Serializable
data class ExposedTeam(val id: Int, val name: String, val codType: String, val fullName: String, val foundation: String,
    val city: String, val codCountry: String, val logoImgFile: String)

class TeamService(database: Database) : BaseService() {
    suspend fun readByName(name: String): ExposedTeam? {
        return dbQuery {
            Teams.selectAll()
                .where { Teams.name eq name }
                .map { ExposedTeam(it[Teams.id].value, it[Teams.name], it[Teams.codType], it[Teams.fullName],
                    it[Teams.foundation], it[Teams.city], it[Teams.codCountry], it[Teams.logoImgFile]) }
                .singleOrNull()
        }
    }
}