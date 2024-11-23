package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MatchTypesTable : IntIdTable() {
    val code = varchar("CODE", 2)
    val description = varchar("DESCRIPTION", 30)

    override val tableName: String
        get() = "MATCH_TYPE"
}

class MatchType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MatchType>(MatchTypesTable)

    var code by MatchTypesTable.code
    var description by MatchTypesTable.description
}