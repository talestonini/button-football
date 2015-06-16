package button.football

class ChampionshipTypeController extends BaseRestfulController {

    ChampionshipTypeController() {
        super(ChampionshipType)
    }

    @Override
    protected List<ChampionshipType> listAllResources(Map params) {
        def teamTypeId = params.teamTypeId
        List<ChampionshipType> result = ChampionshipType.where {
            if (teamTypeId) {
                teamType.id == teamTypeId
            }
        }.findAll()
        result.sort { it.listOrder }
    }

    @Override
    protected ChampionshipType queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        ChampionshipType.where {
            if (teamTypeId) {
                teamType.id == teamTypeId
            }
            id == id
        }.find()
    }
}
