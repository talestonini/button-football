package com.talestonini

import com.talestonini.api.configureTeamApi
import com.talestonini.api.configureUserApi
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    installCors()
    configureSerialization()
    configureApis()
    configureRouting()
}

private fun Application.installCors() {
    install(CORS) {
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Put)
        anyHost() // Don't do this in production if possible. Try to limit it.
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

fun Application.configureApis() {
    configureTeamApi()
    configureUserApi()
}

fun Application.configureRouting() {
    // FIXME: this is just for temporary testing
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

fun Application.getDatabase() =
    Database.connect(
        url = "jdbc:h2:/Users/talestonini/dev/repos/button-football/h2db/buttonfootball",
        user = "sa",
        driver = "org.h2.Driver",
        password = "buttonfootball",
    )

//fun Application.getDatabase() =
//    Database.connect(
//        url = "jdbc:mysql://localhost:3306/buttonfootball",
//        user = "root",
//        driver = "com.mysql.cj.jdbc.Driver",
//        password = "buttonfootball",
//    )