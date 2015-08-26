package button.football

import bfapi.Application
import grails.test.mixin.TestFor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup

@TestFor(TeamController)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
class TeamControllerSpec extends Specification {

    @Autowired
    private WebApplicationContext webAppContext

    private MockMvc mockMvc

    def setup() {
        mockMvc = webAppContextSetup(webAppContext).build()
    }

    def 'test get Corinthians'() {
        given:
        def teamId = 36

        when:
        def result = mockMvc.perform(get("/api/teams/$teamId"))

        then:
        result.andExpect(status().isOk())
    }
}
