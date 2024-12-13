package com.talestonini.api

import com.talestonini.getDatabase
import com.talestonini.service.TeamTypeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureTeamTypeApi() {
    val teamTypeService = TeamTypeService(getDatabase())

    routing {
        get("/teamTypes") {
            val code = call.queryParameters["code"]
            val teams = teamTypeService.read(code)
            if (teams != null) {
                call.respond(HttpStatusCode.OK, teams)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

}