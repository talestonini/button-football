package com.talestonini.model

import com.talestonini.model.MatchesTable.nullable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object MatchesTable : IntIdTable() {
    val idChampionship = reference("ID_CHAMPIONSHIP", ChampionshipsTable.id)
    val codType = reference("COD_TYPE", MatchTypesTable.code)
    val idTeamA = reference("ID_TEAM_A", TeamsTable.id)
    val idTeamB = reference("ID_TEAM_B", TeamsTable.id)
    val numGoalsTeamA = integer("NUM_GOALS_TEAM_A")
    val numGoalsTeamB = integer("NUM_GOALS_TEAM_B")
    val numGoalsExtraA = integer("NUM_GOALS_EXTRA_A")
    val numGoalsExtraB = integer("NUM_GOALS_EXTRA_B")
    val numGoalsPntA = integer("NUM_GOALS_PNT_A")
    val numGoalsPntB = integer("NUM_GOALS_PNT_B")

    override val tableName: String
        get() = "MATCH"
}

class MatchEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MatchEntity>(MatchesTable)

    var championship by ChampionshipEntity referencedOn MatchesTable.idChampionship
    var type by MatchTypeEntity referencedOn MatchesTable.codType
    var teamA by TeamEntity referencedOn MatchesTable.idTeamA
    var teamB by TeamEntity referencedOn MatchesTable.idTeamB
    var numGoalsTeamA by MatchesTable.numGoalsTeamA.nullable()
    var numGoalsTeamB by MatchesTable.numGoalsTeamB.nullable()
    var numGoalsExtraA by MatchesTable.numGoalsExtraA.nullable()
    var numGoalsExtraB by MatchesTable.numGoalsExtraB.nullable()
    var numGoalsPntA by MatchesTable.numGoalsPntA.nullable()
    var numGoalsPntB by MatchesTable.numGoalsPntB.nullable()

    override fun toString() = id.toString()
}