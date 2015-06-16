package button.football

import grails.rest.RestfulController

/**
 * Created by talestonini on 20/05/2015.
 */
abstract class BaseRestfulController extends RestfulController {

    static responseFormats = ['json']

    BaseRestfulController(Class resource) {
        super(resource)
    }

    @Override
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)

        // toArray() is a workaround for bug https://jira.grails.org/browse/GRAILS-11892
        respond listAllResources(params).toArray(), model: [("${resourceName}Count".toString()): countResources()]
    }
}
