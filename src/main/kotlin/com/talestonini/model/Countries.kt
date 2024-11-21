package com.talestonini.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Countries : IntIdTable() {
    val code = varchar("CODE", 3)
    val name = varchar("NAME", 30)

    override val tableName: String
        get() = "COUNTRY"
}