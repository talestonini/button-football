package button.football

class GameController extends AbstractRestfulController {

    GameController() {
        super(Game)
    }

    @Override
    protected List<Game> listAllResources(Map params) {
        Long championshipId = params.championshipId as Long
        def gameTypeDesc = params.gameType
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
        }[0]
    }
}
