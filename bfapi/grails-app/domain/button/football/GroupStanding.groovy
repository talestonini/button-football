package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GroupStanding {

    Championship championship
    GroupType groupType
    Team team
    Short numIntraGrpPos
    Short numExtraGrpPos
    Integer numPoints
    Integer numGames
    Integer numWins
    Integer numDraws
    Integer numLosses
    Integer numScoredGoals
    Integer numConcededGoals
    Integer numDiffGoals

    static belongsTo = [Championship, GroupType, Team]

    static mapping = {
        version false
    }

    static constraints = {
        numIntraGrpPos nullable: true
        numExtraGrpPos nullable: true
    }
}
