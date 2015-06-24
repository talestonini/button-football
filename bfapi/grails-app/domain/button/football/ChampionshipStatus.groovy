package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
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
    String toString() {
        description
    }
}
