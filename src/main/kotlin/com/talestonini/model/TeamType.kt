package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TeamTypesTable : IntIdTable() {
    val code = varchar("CODE", 1)
    val description = varchar("DESCRIPTION", 10)

    override val tableName: String
        get() = "TEAM_TYPE"
}

class TeamType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TeamType>(TeamTypesTable)

    var code by TeamTypesTable.code
    var description by TeamTypesTable.description

    override fun toString() = description
}