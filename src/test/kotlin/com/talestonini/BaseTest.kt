package com.talestonini

import org.jetbrains.exposed.sql.Database

abstract class BaseTest {

    companion object {
        private var db: Database? = null

        fun database(): Database? {
            if (db == null) {
                db = Database.connect(
                    url = "jdbc:h2:./h2/db/buttonfootball",
                    driver = "org.h2.Driver",
                    user = "sa",
                    password = "buttonfootball"
                )
            }
            return db
        }
    }

}