package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ChampionshipStatusesTable : IntIdTable() {
    val code = varchar("CODE", 1)
    val description = varchar("DESCRIPTION", 20)

    override val tableName: String
        get() = "CHAMPIONSHIP_STATUS"
}

class ChampionshipStatus(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChampionshipStatus>(ChampionshipStatusesTable)

    var code by ChampionshipStatusesTable.code
    var description by ChampionshipStatusesTable.description
}