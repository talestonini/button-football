package com.talestonini.api

import com.talestonini.getDatabase
import com.talestonini.service.TeamService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureTeamApi() {
    val teamService = TeamService(getDatabase())

    routing {
        get("/teams") {
            val name = call.queryParameters["name"]
            val teams = teamService.read(name)
            if (teams != null) {
                call.respond(HttpStatusCode.OK, teams)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }

}