package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GameType {

    String description

    // static hasMany = [games: Game]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 30
    }

    @Override
    String toString() {
        description
    }
}
