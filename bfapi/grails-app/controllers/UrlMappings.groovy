class UrlMappings {

    static mappings = {
        "/api/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        '/'(view: '/index')
        '500'(view: '/error')
        '404'(view: '/notFound')

        '/api/teamTypes'(resources: 'teamType')

        group "/api/teamTypes/$teamTypeId" {
            "/championshipTypes"(controller: 'championshipType', action: 'list')
            "/championshipTypes/$id?"(resources: 'championshipType')
        }

        '/api/championshipTypes'(resources: 'championshipType')
    }
}
