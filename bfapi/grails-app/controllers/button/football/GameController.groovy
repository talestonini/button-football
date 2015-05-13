package button.football

import grails.rest.RestfulController

class GameController extends RestfulController {

    static responseFormats = ['json']

    GameController() {
        super(Game)
    }

    @Override
    protected List<Game> listAllResources(Map params) {
        def championshipId = params.championshipId
        Game.where {
            if (championshipId) championship.id == championshipId
        }.findAll()
    }

    @Override
    protected Game queryForResource(Serializable id) {
        def championshipId = params.championshipId
        Game.where {
            if (championshipId) championship.id == championshipId
            id == id
        }.find()
    }
}
