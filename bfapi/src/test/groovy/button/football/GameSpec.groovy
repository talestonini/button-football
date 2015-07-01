package button.football

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification

@TestFor(Game)
@TestMixin(DomainClassUnitTestMixin)
class GameSpec extends Specification {

    ChampionshipType aChampionshipType = new ChampionshipType(name: 'Campeonato Brasileiro')
    Championship aChampionship = new Championship(championshipType: aChampionshipType, numEdition: 2)
    Game game1 = new Game(), game2 = new Game(), game3 = new Game(), game4 = new Game()

    def setup() {
        game1.with {
            championship = aChampionship
            gameType = new GameType(description: 'Group A 1')
            homeTeam = new Team(name: 'Corinthians')
            awayTeam = new Team(name: 'Palmeiras')
        }

        game2.with {
            championship = aChampionship
            gameType = new GameType(description: 'Group B 3')
            homeTeam = new Team(name: 'Flamengo')
            awayTeam = new Team(name: 'Vasco')
            numHomeTeamGoals = 0
            numAwayTeamGoals = 0
        }

        game3.with {
            championship = aChampionship
            gameType = new GameType(description: 'Semi Final AD')
            homeTeam = new Team(name: 'Cruzeiro')
            awayTeam = new Team(name: 'Atlético Mineiro')
            numHomeTeamGoals = 2
            numAwayTeamGoals = 2
            numHomeTeamExtraGoals = 1
            numAwayTeamExtraGoals = 0
        }

        game4.with {
            championship = aChampionship
            gameType = new GameType(description: 'Grand Final')
            homeTeam = new Team(name: 'Grêmio')
            awayTeam = new Team(name: 'Internacional')
            numHomeTeamGoals = 1
            numAwayTeamGoals = 1
            numHomeTeamExtraGoals = 0
            numAwayTeamExtraGoals = 0
            numHomeTeamPntGoals = 4
            numAwayTeamPntGoals = 5
        }
    }

    void "test toString for game with no scores"() {
        when:
        def toString = game1.toString()
        then:
        toString == 'Corinthians x Palmeiras, Group A 1, Campeonato Brasileiro ed 2'
    }

    void "test toString for game with main scores"() {
        when:
        def toString = game2.toString()
        then:
        toString == 'Flamengo 0 x Vasco 0, Group B 3, Campeonato Brasileiro ed 2'
    }

    void "test toString for game with main and extra-time scores"() {
        when:
        def toString = game3.toString()
        then:
        toString == 'Cruzeiro 2-1 x Atlético Mineiro 2-0, Semi Final AD, Campeonato Brasileiro ed 2'
    }

    void "test toString for game with main, extra-time and penalties scores"() {
        when:
        def toString = game4.toString()
        then:
        toString == 'Grêmio 1-0(4) x Internacional 1-0(5), Grand Final, Campeonato Brasileiro ed 2'
    }
}
