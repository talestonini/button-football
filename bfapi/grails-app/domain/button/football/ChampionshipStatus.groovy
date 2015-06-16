package button.football

class ChampionshipStatus {

    String description

    // static hasMany = [championships: Championship]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 20
    }

    @Override
    boolean equals(Object obj) {
        if (!obj instanceof ChampionshipStatus) {
            return false
        } else {
            ChampionshipStatus other = (ChampionshipStatus) obj
            return this.id == other.id
        }
    }

    @Override
    int hashCode() {
        return this.id.hashCode()
    }

    @Override
    String toString() {
        return description
    }
}
