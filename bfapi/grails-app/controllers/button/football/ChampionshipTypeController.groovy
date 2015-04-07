package button.football

import grails.rest.RestfulController

class ChampionshipTypeController extends RestfulController {

    static responseFormats = ['json']

    ChampionshipTypeController() {
        super(ChampionshipType)
    }
}
