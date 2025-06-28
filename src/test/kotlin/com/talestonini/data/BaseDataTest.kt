package com.talestonini.data

import com.talestonini.BaseTest
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseDataTest : BaseTest() {

    fun dataTest(block: () -> Unit) = testApplication {
        database()
        // for when running the database in server mode and willing to execute tests
        //database("jdbc:h2:tcp://localhost:9092/buttonfootball")
        transaction {
            block()
        }
    }

}