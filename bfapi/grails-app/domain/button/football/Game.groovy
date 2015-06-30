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
        String homeScore = '', awayScore = ''

        if (numHomeTeamGoals != null) {
            homeScore = " $numHomeTeamGoals"
        }
        if (numHomeTeamExtraGoals != null) {
            homeScore += "-$numHomeTeamExtraGoals"
        }
        if (numHomeTeamPntGoals != null) {
            homeScore += "($numHomeTeamPntGoals)"
        }

        if (numAwayTeamGoals != null) {
            awayScore = " $numAwayTeamGoals"
        }
        if (numAwayTeamExtraGoals != null) {
            awayScore += "-$numAwayTeamExtraGoals"
        }
        if (numAwayTeamPntGoals != null) {
            awayScore += "($numAwayTeamPntGoals)"
        }

        "$homeTeam.name$homeScore x $awayTeam.name$awayScore, $gameType, $championship"
    }
}
