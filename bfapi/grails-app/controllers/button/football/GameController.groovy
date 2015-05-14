package button.football

import grails.rest.RestfulController

class GameController extends RestfulController {

    static responseFormats = ['json']

    GameController() {
        super(Game)
    }

    @Override
    protected List<Game> listAllResources(Map params) {
        Long championshipId = params.championshipId as Long
        def gameTypeDesc = params.gameTypeDesc
        Game.withCriteria {
            if (championshipId) eq 'championship.id', championshipId
            if (gameTypeDesc) gameType { like 'description', "%$gameTypeDesc%" }
            maxResults 64
            order 'gameType'
        }
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
