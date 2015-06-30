package button.football

import groovy.transform.EqualsAndHashCode
import org.gcontracts.annotations.*

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
        "$homeTeam.name${score('Home')} x $awayTeam.name${score('Away')}, $gameType, $championship"
    }

    @Requires({ team in ['Home', 'Away'] })
    private String score(String team) {
        // main score
        def field = "num${team}TeamGoals"
        String theScore = this[field] != null ? " ${this[field]}" : ''

        // extra-time score
        field = "num${team}TeamExtraGoals"
        theScore += this[field] != null ? "-${this[field]}" : ''

        // penalties score
        field = "num${team}TeamPntGoals"
        theScore += this[field] != null ? "(${this[field]})" : ''

        return theScore
    }
}
