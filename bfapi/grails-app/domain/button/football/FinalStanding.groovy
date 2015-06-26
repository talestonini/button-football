package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class FinalStanding {

    Championship championship
    Team team
    Short numFinalPos
    Integer numPoints
    Integer numGames
    Integer numWins
    Integer numDraws
    Integer numLosses
    Integer numScoredGoals
    Integer numConcededGoals
    Integer numDiffGoals

    static belongsTo = [Championship, Team]

    static mapping = {
        version false
    }

    @Override
    String toString() {
        "$numFinalPos - $team, $championship"
    }
}
