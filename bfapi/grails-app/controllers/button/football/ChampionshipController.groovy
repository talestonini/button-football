package button.football

class ChampionshipController extends AbstractRestfulController {

    ChampionshipController() {
        super(Championship)
    }

    @Override
    protected List<Championship> listAllResources(Map params) {
        def championshipTypeId = params.championshipTypeId
        List<Championship> result = Championship.where {
            if (championshipTypeId) championshipType.id == championshipTypeId
        }.findAll()
        result.sort { it.numEdition }
    }

    @Override
    protected Championship queryForResource(Serializable id) {
        def championshipTypeId = params.championshipTypeId
        Championship.where {
            if (championshipTypeId) championshipType.id == championshipTypeId
            id == id
        }.find()
    }
}
