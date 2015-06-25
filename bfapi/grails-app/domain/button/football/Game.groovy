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
        if (numHomeTeamGoals != null || numAwayTeamGoals != null) {
            homeScore = " $numHomeTeamGoals"
            awayScore = " $numAwayTeamGoals"
            if (!(gameType ==~ /Group.*/)
                    && (numHomeTeamGoals == numAwayTeamGoals)
                    && (numHomeTeamExtraGoals != null || numAwayTeamExtraGoals != null)) {
                homeScore += "-$numHomeTeamExtraGoals"
                awayScore += "-$numAwayTeamExtraGoals"
                if ((numHomeTeamExtraGoals == numAwayTeamExtraGoals)
                        && (numHomeTeamPntGoals != null || numAwayTeamPntGoals != null)) {
                    homeScore += "-$numHomeTeamPntGoals"
                    awayScore += "-$numAwayTeamPntGoals"
                }
            }
        }
        "$homeTeam.name$homeScore x $awayTeam.name$awayScore, $gameType, $championship"
    }
}
