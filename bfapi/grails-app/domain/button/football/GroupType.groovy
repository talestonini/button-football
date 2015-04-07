package button.football

class GroupType {

    String description

    static hasMany = [groupStandings: GroupStanding]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 10
    }
}
