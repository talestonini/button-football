package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ChampionshipTypesTable : IntIdTable() {
    val code = varchar("CODE", 2)
    val description = varchar("DESCRIPTION", 40)
    val codTeamType = reference("COD_TEAM_TYPE", TeamTypesTable.code)
    val numEditions = integer("NUM_EDITIONS")
    val listOrder = integer("LIST_ORDER")
    val logoImgFile = varchar("LOGO_IMG_FILE", 255)

    override val tableName: String
        get() = "CHAMPIONSHIP_TYPE"
}

class ChampionshipType(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChampionshipType>(ChampionshipTypesTable)

    var code by ChampionshipTypesTable.code
    var description by ChampionshipTypesTable.description
    var teamType by TeamType referencedOn ChampionshipTypesTable.codTeamType
    var numEditions by ChampionshipTypesTable.numEditions
    var listOrder by ChampionshipTypesTable.listOrder
    var logoImgFile by ChampionshipTypesTable.logoImgFile

    override fun toString() = description
}