package com.talestonini.model

import com.talestonini.model.TeamsTable.nullable
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

class Match(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Match>(MatchesTable)

    var championship by Championship referencedOn MatchesTable.id
    var type by MatchType referencedOn MatchesTable.codType
    var teamA by Team referencedOn MatchesTable.idTeamA
    var teamB by Team referencedOn MatchesTable.idTeamB
    var numGoalsTeamA by MatchesTable.numGoalsTeamA
    var numGoalsTeamB by MatchesTable.numGoalsTeamB
    var numGoalsExtraA by MatchesTable.numGoalsExtraA.nullable()
    var numGoalsExtraB by MatchesTable.numGoalsExtraB.nullable()
    var numGoalsPntA by MatchesTable.numGoalsPntA.nullable()
    var numGoalsPntB by MatchesTable.numGoalsPntB.nullable()
}