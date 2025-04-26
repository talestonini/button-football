package com.talestonini.service

import com.talestonini.model.Team
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExpTeam(val id: Int, val name: String, val type: String, val fullName: String, val foundation: String,
                   val city: String, val country: String, val logoImgFile: String)

class TeamService(database: Database) : BaseService() {
    suspend fun read(name: String?): List<ExpTeam?> {
        return dbQuery {
            Team.all()
                .filter { if (name != null) it.name == name else true }
                .map { toExpTeam(it) }
        }
    }

    companion object {
        fun toExpTeam(team: Team): ExpTeam =
            ExpTeam(
                team.id.value,
                team.name,
                team.type.description,
                team.fullName,
                team.foundation,
                team.city,
                team.country.name,
                team.logoImgFile
            )
    }
}