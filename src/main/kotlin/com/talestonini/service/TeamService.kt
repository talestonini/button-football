package com.talestonini.service

import com.talestonini.model.TeamEntity
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

data class Team(
    val id: Int?, val name: String, val type: TeamType, val fullName: String, val foundation: String, val city: String,
    val country: Country, val logoImgFile: String,
) {
    constructor(
        name: String, type: TeamType, fullName: String, foundation: String, city: String,
        country: Country, logoImgFile: String,
    ) : this(null, name, type, fullName, foundation, city, country, logoImgFile)
}

@Serializable
data class TeamApiView(
    val id: Int, val name: String, val type: String, val fullName: String, val foundation: String, val city: String,
    val country: String, val logoImgFile: String,
)

class TeamService(database: Database) : BaseService() {

    companion object {
        fun toTeam(teamEntity: TeamEntity): Team =
            Team(
                teamEntity.name,
                TeamTypeService.toTeamType(teamEntity.type),
                teamEntity.fullName,
                teamEntity.foundation,
                teamEntity.city,
                CountryService.toCountry(teamEntity.country),
                teamEntity.logoImgFile
            )

        fun toTeamApiView(teamEntity: TeamEntity): TeamApiView =
            TeamApiView(
                teamEntity.id.value,
                teamEntity.name,
                teamEntity.type.description,
                teamEntity.fullName,
                teamEntity.foundation,
                teamEntity.city,
                teamEntity.country.name,
                teamEntity.logoImgFile
            )
    }

    suspend fun read(name: String?): List<TeamApiView?> {
        return dbQuery {
            TeamEntity.all()
                .filter { if (name != null) it.name == name else true }
                .map { toTeamApiView(it) }
        }
    }

}