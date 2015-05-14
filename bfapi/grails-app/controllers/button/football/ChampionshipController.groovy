package button.football

import grails.rest.RestfulController

class ChampionshipController extends RestfulController {

    static responseFormats = ['json']

    ChampionshipController() {
        super(Championship)
    }

    @Override
    protected List<Championship> listAllResources(Map params) {
        def teamTypeId = params.teamTypeId
        def championshipTypeId = params.championshipTypeId
        Championship.where {
            if (teamTypeId) championshipType.teamType.id == teamTypeId
            if (championshipTypeId) championshipType.id == championshipTypeId
        }.findAll(sort: 'numEdition')
    }

    @Override
    protected Championship queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        def championshipTypeId = params.championshipTypeId
        Championship.where {
            if (teamTypeId) championshipType.teamType.id == teamTypeId
            if (championshipTypeId) championshipType.id == championshipTypeId
            id == id
        }.find()
    }
}
