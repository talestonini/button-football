package button.football

class Championship {

    ChampionshipType championshipType
    Integer numEdition
    Date dtCreated
    Date dtFinished
    Short numTeams
    Short numClassif
    ChampionshipStatus championshipStatus

    // static hasMany = [games: Game,
    //                   groupStandings: GroupStanding,
    //                   finalStandings: FinalStanding]

    static belongsTo = [ChampionshipStatus, ChampionshipType]

    static mapping = {
        version false
    }

    static constraints = {
        dtCreated maxSize: 10
        dtFinished nullable: true, maxSize: 10
    }

    @Override
    boolean equals(Object obj) {
        if (!obj instanceof Championship) {
            return false
        } else {
            Championship other = (Championship) obj
            return this.id == other.id
        }
    }

    @Override
    int hashCode() {
        return this.id.hashCode()
    }

    @Override
    String toString() {
        return "${championshipType.name} - ed ${numEdition}"
    }
}
