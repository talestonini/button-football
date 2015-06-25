package button.football

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Game)
class GameSpec extends Specification {

    ChampionshipType championshipType = new ChampionshipType(name: 'Campeonato Brasileiro')
    Championship championship = new Championship(championshipType: championshipType, numEdition: 2)
    Game groupGame1 = new Game()
    Game groupGame2 = new Game()
    Game groupGame3 = new Game()
    Game nonGroupGame1 = new Game()
    Game nonGroupGame2 = new Game()
    Game nonGroupGame3 = new Game()
    Game nullScoresGame = new Game()

    def setup() {
        groupGame1.championship = championship
        groupGame1.gameType = new GameType(description: 'Group A 1')
        groupGame1.homeTeam = new Team(name: 'Corinthians')
        groupGame1.awayTeam = new Team(name: 'Palmeiras')
        groupGame1.numHomeTeamGoals = 5
        groupGame1.numAwayTeamGoals = 0

        groupGame2.championship = championship
        groupGame2.gameType = new GameType(description: 'Group B 4')
        groupGame2.homeTeam = new Team(name: 'Flamengo')
        groupGame2.awayTeam = new Team(name: 'Vasco')
        groupGame2.numHomeTeamGoals = 2
        groupGame2.numAwayTeamGoals = 2
        groupGame2.numHomeTeamExtraGoals = 0
        groupGame2.numAwayTeamExtraGoals = 0

        groupGame3.championship = championship
        groupGame3.gameType = new GameType(description: 'Group C 2')
        groupGame3.homeTeam = new Team(name: 'Cruzeiro')
        groupGame3.awayTeam = new Team(name: 'Bahia')
        groupGame3.numHomeTeamGoals = 1
        groupGame3.numAwayTeamGoals = 1
        groupGame3.numHomeTeamExtraGoals = 0
        groupGame3.numAwayTeamExtraGoals = 0
        groupGame3.numHomeTeamPntGoals = 0
        groupGame3.numAwayTeamPntGoals = 0

        nonGroupGame1.championship = championship
        nonGroupGame1.gameType = new GameType(description: 'Quarter Final A')
        nonGroupGame1.homeTeam = new Team(name: 'Corinthians')
        nonGroupGame1.awayTeam = new Team(name: 'Palmeiras')
        nonGroupGame1.numHomeTeamGoals = 5
        nonGroupGame1.numAwayTeamGoals = 0

        nonGroupGame2.championship = championship
        nonGroupGame2.gameType = new GameType(description: 'Semi Final AD')
        nonGroupGame2.homeTeam = new Team(name: 'Flamengo')
        nonGroupGame2.awayTeam = new Team(name: 'Vasco')
        nonGroupGame2.numHomeTeamGoals = 2
        nonGroupGame2.numAwayTeamGoals = 2
        nonGroupGame2.numHomeTeamExtraGoals = 1
        nonGroupGame2.numAwayTeamExtraGoals = 0

        nonGroupGame3.championship = championship
        nonGroupGame3.gameType = new GameType(description: 'Grand Final')
        nonGroupGame3.homeTeam = new Team(name: 'Cruzeiro')
        nonGroupGame3.awayTeam = new Team(name: 'Bahia')
        nonGroupGame3.numHomeTeamGoals = 1
        nonGroupGame3.numAwayTeamGoals = 1
        nonGroupGame3.numHomeTeamExtraGoals = 0
        nonGroupGame3.numAwayTeamExtraGoals = 0
        nonGroupGame3.numHomeTeamPntGoals = 5
        nonGroupGame3.numAwayTeamPntGoals = 6

        nullScoresGame.championship = championship
        nullScoresGame.gameType = new GameType(description: 'Group A 1')
        nullScoresGame.homeTeam = new Team(name: 'Corinthians')
        nullScoresGame.awayTeam = new Team(name: 'Palmeiras')
    }

    def cleanup() {
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
