package button.football

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Game)
class GameSpec extends Specification {

    ChampionshipType aChampionshipType = new ChampionshipType(name: 'Campeonato Brasileiro')
    Championship aChampionship = new Championship(championshipType: aChampionshipType, numEdition: 2)
    Game groupGame1 = new Game()
    Game groupGame2 = new Game()
    Game groupGame3 = new Game()
    Game nonGroupGame1 = new Game()
    Game nonGroupGame2 = new Game()
    Game nonGroupGame3 = new Game()
    Game nullScoresGame = new Game()

    def setup() {
        groupGame1.with {
            championship = aChampionship
            gameType = new GameType(description: 'Group A 1')
            homeTeam = new Team(name: 'Corinthians')
            awayTeam = new Team(name: 'Palmeiras')
            numHomeTeamGoals = 5
            numAwayTeamGoals = 0
        }

        groupGame2.with {
            championship = aChampionship
            gameType = new GameType(description: 'Group B 4')
            homeTeam = new Team(name: 'Flamengo')
            awayTeam = new Team(name: 'Vasco')
            numHomeTeamGoals = 2
            numAwayTeamGoals = 2
            numHomeTeamExtraGoals = 0
            numAwayTeamExtraGoals = 0
        }

        groupGame3.with {
            championship = aChampionship
            gameType = new GameType(description: 'Group C 2')
            homeTeam = new Team(name: 'Cruzeiro')
            awayTeam = new Team(name: 'Bahia')
            numHomeTeamGoals = 1
            numAwayTeamGoals = 1
            numHomeTeamExtraGoals = 0
            numAwayTeamExtraGoals = 0
            numHomeTeamPntGoals = 0
            numAwayTeamPntGoals = 0
        }

        nonGroupGame1.with {
            championship = aChampionship
            gameType = new GameType(description: 'Quarter Final A')
            homeTeam = new Team(name: 'Corinthians')
            awayTeam = new Team(name: 'Palmeiras')
            numHomeTeamGoals = 5
            numAwayTeamGoals = 0
        }

        nonGroupGame2.with {
            championship = aChampionship
            gameType = new GameType(description: 'Semi Final AD')
            homeTeam = new Team(name: 'Flamengo')
            awayTeam = new Team(name: 'Vasco')
            numHomeTeamGoals = 2
            numAwayTeamGoals = 2
            numHomeTeamExtraGoals = 1
            numAwayTeamExtraGoals = 0
        }

        nonGroupGame3.with {
            championship = aChampionship
            gameType = new GameType(description: 'Grand Final')
            homeTeam = new Team(name: 'Cruzeiro')
            awayTeam = new Team(name: 'Bahia')
            numHomeTeamGoals = 1
            numAwayTeamGoals = 1
            numHomeTeamExtraGoals = 0
            numAwayTeamExtraGoals = 0
            numHomeTeamPntGoals = 5
            numAwayTeamPntGoals = 6
        }

        nullScoresGame.with {
            championship = aChampionship
            gameType = new GameType(description: 'Group A 1')
            homeTeam = new Team(name: 'Corinthians')
            awayTeam = new Team(name: 'Palmeiras')
        }
    }

    void "test toString for group game with only main scores"() {
        when:
        def toString = groupGame1.toString()

        then:
        toString == 'Corinthians 5 x Palmeiras 0, Group A 1, Campeonato Brasileiro ed 2'
    }

    void "test toString for group game with main and extra-time scores"() {
        when:
        def toString = groupGame2.toString()

        then:
        toString == 'Flamengo 2 x Vasco 2, Group B 4, Campeonato Brasileiro ed 2'
    }

    void "test toString for group game with main, extra-time and penalties scores"() {
        when:
        def toString = groupGame3.toString()

        then:
        toString == 'Cruzeiro 1 x Bahia 1, Group C 2, Campeonato Brasileiro ed 2'
    }

    void "test toString for non-group game with only main scores"() {
        when:
        def toString = nonGroupGame1.toString()

        then:
        toString == 'Corinthians 5 x Palmeiras 0, Quarter Final A, Campeonato Brasileiro ed 2'
    }

    void "test toString for non-group game with main and extra-time scores"() {
        when:
        def toString = nonGroupGame2.toString()

        then:
        toString == 'Flamengo 2-1 x Vasco 2-0, Semi Final AD, Campeonato Brasileiro ed 2'
    }

    void "test toString for non-group game with main, extra-time and penalties scores"() {
        when:
        def toString = nonGroupGame3.toString()

        then:
        toString == 'Cruzeiro 1-0-5 x Bahia 1-0-6, Grand Final, Campeonato Brasileiro ed 2'
    }

    void "test toString for game with empty scores"() {
        when:
        def toString = nullScoresGame.toString()

        then:
        toString == 'Corinthians x Palmeiras, Group A 1, Campeonato Brasileiro ed 2'
    }
}
