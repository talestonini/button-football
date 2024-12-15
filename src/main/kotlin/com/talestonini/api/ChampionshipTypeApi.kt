package com.talestonini.api

import com.talestonini.getDatabase
import com.talestonini.service.ChampionshipService
import com.talestonini.service.ChampionshipTypeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureChampionshipTypeApi() {
    val championshipTypeService = ChampionshipTypeService(getDatabase())
    val championshipService = ChampionshipService(getDatabase())

    routing {
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
    }

}