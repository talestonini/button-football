package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object CountriesTable : IntIdTable() {
    val code = varchar("CODE", 3)
    val name = varchar("NAME", 30)

    override val tableName: String
        get() = "COUNTRY"
}

class CountryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CountryEntity>(CountriesTable)

    var code by CountriesTable.code
    var name by CountriesTable.name

    override fun toString() = name
}