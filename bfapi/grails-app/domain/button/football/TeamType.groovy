package button.football

import grails.rest.*

@Resource(uri='/teamTypes', formats=['json'])
class TeamType {

    String description

    static hasMany = [teams: Team,
                      championshipTypes: ChampionshipType,
                      unifiedRankingTypes: UnifiedRankingType]

    static mapping = {
        version false
    }

    static constraints = {
        description maxSize: 20
    }
}
