package button.football

class Score {

    ChampionshipType championshipType
    Short numPos
    Short numPoints

    static belongsTo = [ChampionshipType]

    static mapping = {
        version false
    }
}
