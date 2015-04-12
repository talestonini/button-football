package button.football

import grails.rest.RestfulController

class ChampionshipTypeController extends RestfulController {

    static responseFormats = ['json']

    ChampionshipTypeController() {
        super(ChampionshipType)
    }

    @Override
    protected ChampionshipType queryForResource(Serializable id) {
        ChampionshipType.where {
            id == id && teamType.id == request.JSON.params.teamTypeId
        }.find()
    }
}
