package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class GroupType {

    String description

    // static hasMany = [groupStandings: GroupStanding]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 10
    }

    @Override
    String toString() {
        description
    }
}
