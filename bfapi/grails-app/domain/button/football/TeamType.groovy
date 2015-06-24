package button.football

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class TeamType {

    String description

    // static hasMany = [teams: Team,
    //                   championshipTypes: ChampionshipType,
    //                   unifiedRankingTypes: UnifiedRankingType]

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
