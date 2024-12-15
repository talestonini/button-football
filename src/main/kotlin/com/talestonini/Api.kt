package com.talestonini

import com.talestonini.service.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureButtonFootballApi() {
    val database = getDatabase()
    val teamTypeService = TeamTypeService(database)
    val teamService = TeamService(database)
    val championshipTypeService = ChampionshipTypeService(database)
    val championshipService = ChampionshipService(database)
    val matchService = MatchService(database)

    routing {
        get("/teamTypes") {
            val code = call.queryParameters["code"]
            val teams = teamTypeService.read(code)
            if (teams.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, teams)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/teams") {
            val name = call.queryParameters["name"]
            val teams = teamService.read(name)
            if (teams.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, teams)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championshipTypes") {
            val codTeamType = call.queryParameters["codTeamType"]
            val championshipTypes = championshipTypeService.read(codTeamType)
            if (championshipTypes.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, championshipTypes)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championshipTypes/{id}/championships") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("missing championship type id")
            val championshipType = championshipTypeService.read(id)
            val championships = championshipService.read(championshipType.code)
            if (championships.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, championships)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championships/{id}/matches") {
            val championshipId =
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("missing championship id")
            val codMatchType = call.queryParameters["codMatchType"]
            val matches = matchService.read(championshipId, codMatchType)
            if (matches.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, matches)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

}
