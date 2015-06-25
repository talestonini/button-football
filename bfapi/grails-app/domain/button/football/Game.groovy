package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Game {

    Championship championship
    GameType gameType
    Team homeTeam
    Team awayTeam
    Short numHomeTeamGoals
    Short numAwayTeamGoals
    Short numHomeTeamExtraGoals
    Short numAwayTeamExtraGoals
    Short numHomeTeamPntGoals
    Short numAwayTeamPntGoals

    static belongsTo = [Championship, GameType, Team]

    static mapping = {
        version false
    }

    static constraints = {
        numHomeTeamGoals nullable: true
        numAwayTeamGoals nullable: true
        numHomeTeamExtraGoals nullable: true
        numAwayTeamExtraGoals nullable: true
        numHomeTeamPntGoals nullable: true
        numAwayTeamPntGoals nullable: true
    }

    @Override
    String toString() {
        "$homeTeam.name $numHomeTeamGoals x $awayTeam.name $numAwayTeamGoals, $gameType, $championship.championshipType ed $championship.numEdition"
    }
}
