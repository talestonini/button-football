package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ScoringsTable : IntIdTable() {
    val codChampionshipType = reference("COD_CHAMPIONSHIP_TYPE", ChampionshipTypesTable.code)
    val numPos = integer("NUM_POS")
    val numPoints = integer("NUM_POINTS")

    override val tableName: String
        get() = "SCORING"
}

class ScoringEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ScoringEntity>(ScoringsTable)

    var championshipType by ChampionshipTypeEntity referencedOn ScoringsTable.codChampionshipType
    var numPos by ScoringsTable.numPos
    var numPoints by ScoringsTable.numPoints

    override fun toString() = id.toString()
}