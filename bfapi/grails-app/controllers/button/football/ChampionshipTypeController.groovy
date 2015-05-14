package button.football

import grails.rest.RestfulController

class ChampionshipTypeController extends RestfulController {

    static responseFormats = ['json']

    ChampionshipTypeController() {
        super(ChampionshipType)
    }

    @Override
    protected List<ChampionshipType> listAllResources(Map params) {
        def teamTypeId = params.teamTypeId
        ChampionshipType.where {
            if (teamTypeId) teamType.id == teamTypeId
        }.findAll(sort: 'listOrder')
    }

    @Override
    protected ChampionshipType queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        ChampionshipType.where {
            if (teamTypeId) teamType.id == teamTypeId
            id == id
        }.find()
    }
}
