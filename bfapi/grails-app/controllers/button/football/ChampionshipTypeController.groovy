package button.football

import grails.rest.RestfulController

class ChampionshipTypeController extends RestfulController {

    static responseFormats = ['json']

    ChampionshipTypeController() {
        super(ChampionshipType)
    }

    @Override
    protected ChampionshipType queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        ChampionshipType.where {
            id == id
            teamType.id == teamTypeId
        }.find()
    }
}
