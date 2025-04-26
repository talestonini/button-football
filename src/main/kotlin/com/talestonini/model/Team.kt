package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TeamsTable : IntIdTable() {
    val name = varchar("NAME", 30)
    val codType = reference("COD_TYPE", TeamTypesTable.code)
    val fullName = varchar("FULL_NAME", 60)
    val foundation = varchar("FOUNDATION", 4)
    val city = varchar("CITY", 20)
    val codCountry = reference("COD_COUNTRY", CountriesTable.code)
    val logoImgFile = varchar("LOGO_IMG_FILE", 255)

    override val tableName: String
        get() = "TEAM"
}

class Team(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Team>(TeamsTable)

    var name by TeamsTable.name
    var type by TeamType referencedOn TeamsTable.codType
    var fullName by TeamsTable.fullName
    var foundation by TeamsTable.foundation
    var city by TeamsTable.city
    var country by Country referencedOn TeamsTable.codCountry
    var logoImgFile by TeamsTable.logoImgFile

    override fun toString() = name
}