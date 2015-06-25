package button.football

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(Game)
class GameSpec extends Specification {

    ChampionshipType championshipType = new ChampionshipType(name: 'Campeonato Brasileiro')
    Championship championship = new Championship(championshipType: championshipType, numEdition: 2)
    Game groupGame1 = new Game();
    Game groupGame2 = new Game();
    Game groupGame3 = new Game();

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
        groupGame3.numAwayTeamGoals = 3
        groupGame3.numHomeTeamExtraGoals = 0
        groupGame3.numAwayTeamExtraGoals = 0
        groupGame3.numHomeTeamPntGoals = 0
        groupGame3.numAwayTeamPntGoals = 0
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
        toString == 'Cruzeiro 1 x Bahia 3, Group C 2, Campeonato Brasileiro ed 2'
    }
}
