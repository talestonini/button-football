// Place your Spring DSL code here
import grails.rest.render.json.*
import button.football.*

beans = {
    championshipTypeJSONRenderer(JsonRenderer, ChampionshipType) {
        excludes = ['class']
    }
    championshipTypeJSONCollectionRenderer(JsonCollectionRenderer, ChampionshipType) {
        excludes = ['class']
    }
}
