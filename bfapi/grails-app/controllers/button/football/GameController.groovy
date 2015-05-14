package button.football

import grails.rest.RestfulController
import org.hibernate.FetchMode

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
        Long championshipId = params.championshipId as Long
        (Game) Game.withCriteria {
            if (championshipId) eq 'championship.id', championshipId
            eq 'id', id as Long
            // fetchMode 'homeTeam', FetchMode.JOIN
            // fetchMode 'awayTeam', FetchMode.JOIN
        }[0]
    }
}
