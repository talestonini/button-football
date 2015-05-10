package button.football

import grails.rest.*

//@Resource(uri='/teams', formats=['json'])
class Team {

    String name
    TeamType teamType
    String fullName
    String foundation
    String city
    Country country
    String logoImgFile

    // static hasMany = [gamesForHomeTeam: Game,
    //                   gamesForAwayTeam: Game,
    //                   groupStandings: GroupStanding,
    //                   finalStandings: FinalStanding,
    //                   rankings: Ranking,
    //                   unifiedRankings: UnifiedRanking]

    static belongsTo = [TeamType, Country]

    // static mappedBy = [gamesForHomeTeam: "homeTeam",
    //                    gamesForAwayTeam: "awayTeam"]

    static mapping = {
        version false
    }

    static constraints = {
        name maxSize: 30, unique: true
        fullName nullable: true, maxSize: 60
        foundation nullable: true, maxSize: 4
        city nullable: true, maxSize: 20
        logoImgFile nullable: true
    }
}
