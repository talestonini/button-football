package com.talestonini.data

import com.talestonini.BaseTest
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseDataTest : BaseTest() {

    fun dataTest(fn: () -> Unit) = testApplication {
        database()
        transaction {
            fn()
        }
    }

}