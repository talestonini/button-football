package com.talestonini.app.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Country : IntIdTable() {
    val code = varchar("CODE", 3)
    val name = varchar("NAME", 30)
}