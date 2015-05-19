package button.football

import grails.rest.RestfulController

/**
 * Created by talestonini on 20/05/2015.
 */
class BFRestfulController extends RestfulController {

    static responseFormats = ['json']

    BFRestfulController(Class resource) {
        super(resource)
    }

    @Override
    Object index(Integer max) {
        return ((List) super.index(max)).toArray()
    }
}
