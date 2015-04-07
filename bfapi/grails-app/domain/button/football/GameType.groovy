package button.football

class GameType {

    String description

    static hasMany = [games: Game]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 30
    }
}
