package com.talestonini

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import kotlin.test.*

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

    @Test
    fun testReadUser() = testApplication {
        application {
            configureDatabases()
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
        application {
            configureDatabases()
        }
        install(ContentNegotiation) {
            json()
        }
        client.get("/teams?name=Corinthians").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}