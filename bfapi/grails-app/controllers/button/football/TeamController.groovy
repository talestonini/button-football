package button.football

class TeamController extends BFRestfulController {

    TeamController() {
        super(Team)
    }

    @Override
    protected List<Team> listAllResources(Map params) {
        def teamTypeId = params.teamTypeId
        Team.where {
            if (teamTypeId) teamType.id == teamTypeId
        }.findAll() // (sort: 'name')
    }

    @Override
    protected Team queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        Team.where {
            if (teamTypeId) teamType.id == teamTypeId
            id == id
        }.find()
    }
}
