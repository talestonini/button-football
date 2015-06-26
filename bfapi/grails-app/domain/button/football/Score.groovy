package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Score {

    ChampionshipType championshipType
    Short numPos
    Short numPoints

    static belongsTo = [ChampionshipType]

    static mapping = {
        version false
    }

    @Override
    String toString() {
        "$championshipType: pos $numPos -> $numPoints points"
    }
}
