package button.football

class Scoring {

    ChampionshipType championshipType
    Short numPos
    Short numPoints

    static belongsTo = [ChampionshipType]

    static mapping = {
        version false
    }
}
