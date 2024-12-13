package com.talestonini

import com.talestonini.api.*
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

fun Application.getDatabase(): Database {
    val profile = environment.config.propertyOrNull("ktor.profile")?.getString() ?: "dev"
    log.info("app execution profile: $profile")
    val dbConfig = "ktor.$profile.db"
    return Database.connect(
        url = environment.config.property("$dbConfig.url").getString(),
        driver = environment.config.property("$dbConfig.driver").getString(),
        user = environment.config.property("$dbConfig.user").getString(),
        password = environment.config.property("$dbConfig.password").getString()
    )
}

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

private fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

private fun Application.configureApis() {
    configureTeamTypeApi()
    configureChampionshipTypeApi()
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