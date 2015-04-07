package button.football

class ChampionshipStatus {

    String description

    static hasMany = [championships: Championship]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 20
    }
}
