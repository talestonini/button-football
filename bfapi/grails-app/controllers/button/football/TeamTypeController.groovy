package button.football

import grails.rest.RestfulController

class TeamTypeController extends RestfulController {

    static responseFormats = ['json']

    TeamTypeController() {
        super(TeamType)
    }
}
