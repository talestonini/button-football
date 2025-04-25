package com.talestonini

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseTest {

    private fun initDatabase() =
        Database.connect(
            url = "jdbc:h2:./h2/db/buttonfootball",
            driver = "org.h2.Driver",
            user = "sa",
            password = "buttonfootball"
        )

    fun <T> fromDb(fn: () -> List<T>): List<T> {
        initDatabase()
        var res = emptyList<T>()
        transaction {
            res = fn()
        }
        return res
    }

}