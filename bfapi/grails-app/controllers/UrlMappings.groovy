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

        '/api/teamTypes'(resources: 'teamType') {
            '/championshipTypes'(resources: 'championshipType')
        }

        '/api/championshipTypes'(resources: 'championshipType')
    }
}
