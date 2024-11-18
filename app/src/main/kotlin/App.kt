package com.talestonini.app

import com.talestonini.app.model.Team
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    Database.connect("jdbc:mysql://localhost:3306/buttonfootball", driver = "com.mysql.cj.jdbc.Driver",
        user = "root", password = "buttonfootball")

    transaction {
        addLogger(StdOutSqlLogger)

        val query = Team.selectAll().where { Team.name eq "Corinthians" }

        query.forEach {
            println("${it[Team.name]} was founded in ${it[Team.foundation]}")
        }
    }
}
