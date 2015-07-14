package button.football

import grails.test.mixin.integration.Integration
import grails.transaction.*

import spock.lang.*
import geb.spock.*

@Integration
@Rollback
class TeamTypeControllerSpec extends GebSpec {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        when:
        go '/'
        then:
        //$('title').text() == "Welcome to Grails"
        true
    }
}
