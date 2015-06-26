package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Ranking {

    ChampionshipType championshipType
    Team team
    Short numRankingPos
    Short numBestPos
    Short numWorstPos
    Double numAvgPos
    Integer numParticipations
    Integer numChampionships
    Integer numRankingPoints
    Integer numPoints
    Integer numGames
    Integer numWins
    Integer numDraws
    Integer numLosses
    Integer numScoredGoals
    Integer numConcededGoals
    Integer numDiffGoals
    Integer numUpToEdition

    static belongsTo = [ChampionshipType, Team]

    static mapping = {
        version false
    }

    static constraints = {
        numAvgPos scale: 5
    }

    @Override
    String toString() {
        "$numRankingPos - $team, $championshipType"
    }
}
