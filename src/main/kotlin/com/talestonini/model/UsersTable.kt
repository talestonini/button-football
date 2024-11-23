package com.talestonini.model

import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val id = integer("ID").autoIncrement()
    val name = varchar("NAME", length = 50)
    val age = integer("AGE")

    override val primaryKey = PrimaryKey(id)
    override val tableName: String
        get() = "USER"
}