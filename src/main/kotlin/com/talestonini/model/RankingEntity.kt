package com.talestonini.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object RankingsTable : IntIdTable() {
    val codChampionshipType = reference("COD_CHAMPIONSHIP_TYPE", ChampionshipTypesTable.code)
    val idTeam = reference("ID_TEAM", TeamsTable.id)
    val numBestPos = integer("NUM_BEST_POS")
    val numWorstPos = integer("NUM_WORST_POS")
    val numAvgPos = double("NUM_AVG_POS")
    val numParticipations = integer("NUM_PARTICIPATIONS")
    val numRankingPoints = integer("NUM_RANKING_POINTS")
    val numRankingPos = integer("NUM_RANKING_POS")
    val numPoints = integer("NUM_POINTS")
    val numMatches = integer("NUM_MATCHES")
    val numWins = integer("NUM_WINS")
    val numDraws = integer("NUM_DRAWS")
    val numLosses = integer("NUM_LOSSES")
    val numGoalsScored = integer("NUM_GOALS_SCORED")
    val numGoalsConceded = integer("NUM_GOALS_CONCEDED")
    val numGoalsDiff = integer("NUM_GOALS_DIFF")
    val numChampionships = integer("NUM_CHAMPIONSHIPS")
    val numUpToEdition = integer("NUM_UP_TO_EDITION")

    override val tableName: String
        get() = "RANKING"
}

class RankingEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RankingEntity>(RankingsTable)

    var championshipType by ChampionshipTypeEntity referencedOn RankingsTable.codChampionshipType
    var team by TeamEntity referencedOn RankingsTable.idTeam
    var numBestPos by RankingsTable.numBestPos
    var numWorstPos by RankingsTable.numWorstPos
    var numAvgPos by RankingsTable.numAvgPos
    var numParticipations by RankingsTable.numParticipations
    var numRankingPoints by RankingsTable.numRankingPoints
    var numRankingPos by RankingsTable.numRankingPos
    var numPoints by RankingsTable.numPoints
    var numMatches by RankingsTable.numMatches
    var numWins by RankingsTable.numWins
    var numDraws by RankingsTable.numDraws
    var numLosses by RankingsTable.numLosses
    var numGoalsScored by RankingsTable.numGoalsScored
    var numGoalsConceded by RankingsTable.numGoalsConceded
    var numGoalsDiff by RankingsTable.numGoalsDiff
    var numChampionships by RankingsTable.numChampionships
    var numUpToEdition by RankingsTable.numUpToEdition

    override fun toString() = id.toString()
}