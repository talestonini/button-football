package com.talestonini

import com.talestonini.service.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureButtonFootballApi() {
    val database = database()
    val teamTypeService = TeamTypeService(database)
    val championshipTypeService = ChampionshipTypeService(database)
    val championshipService = ChampionshipService(database)
    val matchTypeService = MatchTypeService(database)
    val matchService = MatchService(database)
    val standingService = StandingService(database)
    val rankingService = RankingService(database)
    val scoringService = ScoringService(database)
    val teamService = TeamService(database)

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

        get("/championships") {
            val codChampionshipType = call.queryParameters["codChampionshipType"]
            val championships = championshipService.read(codChampionshipType)
            if (championships.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, championships)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/matchTypes") {
            val codMatchType = call.queryParameters["codMatchType"]
            val matchTypes = matchTypeService.read(codMatchType)
            if (matchTypes.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, matchTypes)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championships/{id}/matches") {
            val championshipId =
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("missing championship id")
            val codMatchTypes = call.queryParameters["codMatchTypes"]?.split(",")
            val matches = matchService.read(championshipId, codMatchTypes)
            if (matches.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, matches)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championships/{id}/standings") {
            val championshipId =
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("missing championship id")
            val standings = standingService.read(championshipId)
            if (standings.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, standings)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championships/{id}/groupStandings") {
            val championshipId =
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("missing championship id")
            val standings = standingService.read(championshipId, ('A'..'Z').map { g -> "Grupo $g" })
            if (standings.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, standings)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/championships/{id}/finalStandings") {
            val championshipId =
                call.parameters["id"]?.toInt() ?: throw IllegalStateException("missing championship id")
            val standings = standingService.read(championshipId, listOf("Final"))
            if (standings.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, standings)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/rankings") {
            val codChampionshipType = call.queryParameters["codChampionshipType"]
                ?: throw IllegalStateException("missing codChampionshipType")
            val numUpToEdition = call.queryParameters["numUpToEdition"]?.toInt()
                ?: throw IllegalStateException("missing numUpToEdition")
            val rankings = rankingService.read(codChampionshipType, numUpToEdition)
            if (rankings.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, rankings)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/scorings") {
            val codChampionshipType = call.queryParameters["codChampionshipType"]
                ?: throw IllegalStateException("missing codChampionshipType")
            val scorings = scoringService.read(codChampionshipType)
            if (scorings.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, scorings)
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
    }

}
