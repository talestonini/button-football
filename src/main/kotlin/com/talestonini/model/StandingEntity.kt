package com.talestonini.model

import com.talestonini.model.StandingsTable.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object StandingsTable : IntIdTable() {
    val idChampionship = reference("ID_CHAMPIONSHIP", ChampionshipsTable.id)
    val idTeam = reference("ID_TEAM", TeamsTable.id)
    val codMatchType = reference("COD_MATCH_TYPE", MatchTypesTable.code)
    val numIntraGrpPos = integer("NUM_INTRA_GRP_POS")
    val numExtraGrpPos = integer("NUM_EXTRA_GRP_POS")
    val numFinalPos = integer("NUM_FINAL_POS")
    val numPoints = integer("NUM_POINTS")
    val numMatches = integer("NUM_MATCHES")
    val numWins = integer("NUM_WINS")
    val numDraws = integer("NUM_DRAWS")
    val numLosses = integer("NUM_LOSSES")
    val numGoalsScored = integer("NUM_GOALS_SCORED")
    val numGoalsConceded = integer("NUM_GOALS_CONCEDED")
    val numGoalsDiff = integer("NUM_GOALS_DIFF")

    override val tableName: String
        get() = "STANDING"
}

class StandingEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StandingEntity>(StandingsTable)

    var championship by ChampionshipEntity referencedOn StandingsTable.idChampionship
    var team by TeamEntity referencedOn StandingsTable.idTeam
    var matchType by MatchTypeEntity referencedOn StandingsTable.codMatchType
    var numIntraGrpPos by StandingsTable.numIntraGrpPos.nullable()
    var numExtraGrpPos by StandingsTable.numExtraGrpPos.nullable()
    var numFinalPos by StandingsTable.numFinalPos.nullable()
    var numPoints by StandingsTable.numPoints
    var numMatches by StandingsTable.numMatches
    var numWins by StandingsTable.numWins
    var numDraws by StandingsTable.numDraws
    var numLosses by StandingsTable.numLosses
    var numGoalsScored by StandingsTable.numGoalsScored
    var numGoalsConceded by StandingsTable.numGoalsConceded
    var numGoalsDiff by StandingsTable.numGoalsDiff

    override fun toString() = id.toString()
}