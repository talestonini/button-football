// Place your Spring DSL code here
import grails.rest.render.json.*
import button.football.*

beans = {
    championshipJSONRenderer(JsonRenderer, Championship) {
        excludes = ['class']
    }
    championshipJSONCollectionRenderer(JsonCollectionRenderer, Championship) {
        excludes = ['class']
    }

    championshipStatusJSONRenderer(JsonRenderer, ChampionshipStatus) {
        excludes = ['class']
    }
    championshipStatusJSONCollectionRenderer(JsonCollectionRenderer, ChampionshipStatus) {
        excludes = ['class']
    }

    championshipTypeJSONRenderer(JsonRenderer, ChampionshipType) {
        excludes = ['class']
    }
    championshipTypeJSONCollectionRenderer(JsonCollectionRenderer, ChampionshipType) {
        excludes = ['class']
    }

    countryJSONRenderer(JsonRenderer, Country) {
        excludes = ['class']
    }
    countryJSONCollectionRenderer(JsonCollectionRenderer, Country) {
        excludes = ['class']
    }

    finalStandingJSONRenderer(JsonRenderer, FinalStanding) {
        excludes = ['class']
    }
    finalStandingJSONCollectionRenderer(JsonCollectionRenderer, FinalStanding) {
        excludes = ['class']
    }

    gameJSONRenderer(JsonRenderer, Game) {
        excludes = ['class']
    }
    gameJSONCollectionRenderer(JsonCollectionRenderer, Game) {
        excludes = ['class']
    }

    groupStandingJSONRenderer(JsonRenderer, GroupStanding) {
        excludes = ['class']
    }
    groupStandingJSONCollectionRenderer(JsonCollectionRenderer, GroupStanding) {
        excludes = ['class']
    }

    groupTypeJSONRenderer(JsonRenderer, GroupType) {
        excludes = ['class']
    }
    groupTypeJSONCollectionRenderer(JsonCollectionRenderer, GroupType) {
        excludes = ['class']
    }

    rankingJSONRenderer(JsonRenderer, Ranking) {
        excludes = ['class']
    }
    rankingJSONCollectionRenderer(JsonCollectionRenderer, Ranking) {
        excludes = ['class']
    }

    scoreJSONRenderer(JsonRenderer, Score) {
        excludes = ['class']
    }
    scoreJSONCollectionRenderer(JsonCollectionRenderer, Score) {
        excludes = ['class']
    }

    teamJSONRenderer(JsonRenderer, Team) {
        excludes = ['class']
    }
    teamJSONCollectionRenderer(JsonCollectionRenderer, Team) {
        excludes = ['class']
    }

    teamTypeJSONRenderer(JsonRenderer, TeamType) {
        excludes = ['class']
    }
    teamTypeJSONCollectionRenderer(JsonCollectionRenderer, TeamType) {
        excludes = ['class']
    }

    unifiedRankingJSONRenderer(JsonRenderer, UnifiedRanking) {
        excludes = ['class']
    }
    unifiedRankingJSONCollectionRenderer(JsonCollectionRenderer, UnifiedRanking) {
        excludes = ['class']
    }

    unifiedRankingTypeJSONRenderer(JsonRenderer, UnifiedRankingType) {
        excludes = ['class']
    }
    unifiedRankingTypeJSONCollectionRenderer(JsonCollectionRenderer, UnifiedRankingType) {
        excludes = ['class']
    }
}
