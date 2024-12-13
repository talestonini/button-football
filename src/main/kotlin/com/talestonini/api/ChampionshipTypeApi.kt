package com.talestonini.api

import com.talestonini.getDatabase
import com.talestonini.service.ChampionshipTypeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureChampionshipTypeApi() {
    val championshipTypeService = ChampionshipTypeService(getDatabase())

    routing {
        get("/championshipTypes") {
            val codTeamType = call.queryParameters["codTeamType"]
            val championshipTypes = championshipTypeService.read(codTeamType)
            if (championshipTypes != null) {
                call.respond(HttpStatusCode.OK, championshipTypes)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

}