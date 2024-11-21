package com.talestonini.model

import org.jetbrains.exposed.dao.id.IntIdTable

object TeamTypes : IntIdTable() {
    val code = varchar("CODE", 1)
    val description = varchar("DESCRIPTION", 10)

    override val tableName: String
        get() = "TEAM_TYPE"
}