package button.football

import grails.rest.RestfulController

class ChampionshipTypeController extends RestfulController {

    static responseFormats = ['json']

    ChampionshipTypeController() {
        super(ChampionshipType)
    }

    @Override
    protected List<ChampionshipType> listAllResources(Map params) {
        ChampionshipType.where {
            teamType.id == params.teamTypeId
        }.findAll()
    }

    @Override
    protected ChampionshipType queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        ChampionshipType.where {
            teamType.id == teamTypeId
            id == id
        }.find()
    }
}
