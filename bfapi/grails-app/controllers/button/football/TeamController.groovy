package button.football

class TeamController extends BaseRestfulController {

    TeamController() {
        super(Team)
    }

    @Override
    protected List<Team> listAllResources(Map params) {
        def teamTypeId = params.teamTypeId
        List<Team> result = Team.where {
            if (teamTypeId) {
                teamType.id == teamTypeId
            }
        }.findAll()
        result.sort { it.name }
    }

    @Override
    protected Team queryForResource(Serializable id) {
        def teamTypeId = params.teamTypeId
        Team.where {
            if (teamTypeId) {
                teamType.id == teamTypeId
            }
            id == id
        }.find()
    }
}
