package com.talestonini.model

import com.talestonini.model.ChampionshipsTable.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ChampionshipsTable : IntIdTable() {
    val codType = reference("COD_TYPE", ChampionshipTypesTable.code)
    val numEdition = integer("NUM_EDITION")
    val dtCreation = varchar("DT_CREATION", 10)
    val dtEnd = varchar("DT_END", 10)
    val numTeams = integer("NUM_TEAMS")
    val numQualif = integer("NUM_QUALIF")
    val codStatus = reference("COD_STATUS", ChampionshipStatusesTable.code)

    override val tableName: String
        get() = "CHAMPIONSHIP"
}

class Championship(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Championship>(ChampionshipsTable)

    var type by ChampionshipType referencedOn ChampionshipsTable.codType
    var numEdition by ChampionshipsTable.numEdition
    var dtCreation by ChampionshipsTable.dtCreation
    var dtEnd by ChampionshipsTable.dtEnd.nullable()
    var numTeams by ChampionshipsTable.numTeams
    var numQualif by ChampionshipsTable.numQualif
    var status by ChampionshipStatus referencedOn ChampionshipsTable.codStatus
}