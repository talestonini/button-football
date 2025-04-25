package com.talestonini

import com.talestonini.user.configureUserApi
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.yaml.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }

    @Disabled
    @Test
    fun testReadUser() = testApplication {
        application {
            configureUserApi()
        }
        install(ContentNegotiation) {
            json()
        }
        client.get("/users/1").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testReadTeamByName() = testApplication {
        environment {
            config = YamlConfigLoader().load("application.yaml")!!
        }
        application {
            configureButtonFootballApi()
        }
        client.get("/teams?name=Corinthians").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

}