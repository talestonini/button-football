package com.talestonini.app.model

import org.jetbrains.exposed.dao.id.IntIdTable

object TeamType : IntIdTable() {
    val code = varchar("CODE", 1)
    val description = varchar("DESCRIPTION", 10)
}