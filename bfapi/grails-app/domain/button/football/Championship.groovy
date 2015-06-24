package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Championship {

    ChampionshipType championshipType
    Integer numEdition
    Date dtCreated
    Date dtFinished
    Short numTeams
    Short numClassif
    ChampionshipStatus championshipStatus

    // static hasMany = [games: Game,
    //                   groupStandings: GroupStanding,
    //                   finalStandings: FinalStanding]

    static belongsTo = [ChampionshipStatus, ChampionshipType]

    static mapping = {
        version false
    }

    static constraints = {
        dtCreated maxSize: 10
        dtFinished nullable: true, maxSize: 10
    }

    @Override
    String toString() {
        "$championshipType.name - ed $numEdition"
    }
}
