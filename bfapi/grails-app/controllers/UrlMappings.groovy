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

        // main screen

        '/api/teamTypes'(resources: 'teamType')

        '/api/teamTypes'(resources: 'teamType') {
            '/championshipTypes'(resources: 'championshipType')
        }

        '/api/championshipTypes'(resources: 'championshipType') {
            '/championships'(resources: 'championship')
        }

        '/api/championships'(resources: 'championship') {
            '/games'(resources: 'game')
        }

        // teams screen

        '/api/teams'(resources: 'team')

        '/api/teamTypes'(resources: 'teamType') {
            '/teams'(resources: 'team')
        }

        // any screen

        '/api/games'(resources: 'game')
    }
}
