package com.talestonini

import com.talestonini.service.*
import org.jetbrains.exposed.sql.Database

abstract class BaseTest {

    companion object {
        private var db: Database? = null

        fun database(url: String = "jdbc:h2:./h2/db/buttonfootball"): Database? {
            if (db == null) {
                db = Database.connect(url, driver = "org.h2.Driver", user = "sa", password = "buttonfootball")
            }
            return db
        }

        val anyChampionshipType = ChampionshipType("ct", "anyChampionship", 1, "")
        val anyChampionshipStatus = ChampionshipStatus("cs", "anyChampionshipStatus")
        val anyChampionship = Championship(anyChampionshipType, 1, "", "", 8, 4, anyChampionshipStatus)
        val anyCountry = Country("c", "anyCountry")
        val anyTeamType = TeamType("tt", "anyTeamType")
        val anyTeam = Team("anyTeam", anyTeamType, "", "1900", "anyCity", anyCountry, "")
        val anyOtherTeam = Team("anyOtherTeam", anyTeamType, "", "1901", "anyOtherCity", anyCountry, "")
        val anyMatchType = MatchType("mt", "anyMatchType")
        val anyMatch = Match(anyChampionship, anyMatchType, anyTeam, anyOtherTeam, 0, 0, null, null, null, null)
        val anyStanding = Standing(anyChampionship, anyTeam, anyMatchType, null, null, null, 0, 0, 0, 0, 0,
            isIgpUntiedByHeadToHead = false, isIgpUntiedRandomly = false, isEgpUntiedRandomly = false,
            isFpUntiedByCampaign = false, isFpUntiedRandomly = false)
    }

}