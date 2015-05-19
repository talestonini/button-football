package button.football

class ChampionshipController extends BFRestfulController {

    ChampionshipController() {
        super(Championship)
    }

    @Override
    protected List<Championship> listAllResources(Map params) {
        def championshipTypeId = params.championshipTypeId
        Championship.where {
            if (championshipTypeId) championshipType.id == championshipTypeId
        }.findAll() // (sort: 'numEdition')
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
