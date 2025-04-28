package com.talestonini.data

import com.talestonini.model.*
import com.talestonini.service.ManagementService
import com.talestonini.service.MatchService
import com.talestonini.service.Standing
import com.talestonini.service.StandingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class GroupStandingsTest : BaseDataTest() {

    @Disabled
    @Test
    fun `group standings should generate a standing per team in the group`() = dataTest {
        ChampionshipEntity.all()
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                (1..c.numTeams/4).forEach { g ->
                    println(">>> testing championship ${c.id.value} group g${g}")

                    val matches = MatchEntity.all()
                        .filter { it.championship == c && it.type.code == "g$g" }
                        .map { MatchService.toMatch(it) }
                        .toSet()
                    val calcStandings = ManagementService.groupStandings(matches)

                    val dbStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code == "g$g" }
                        .map {
                            // must null the extra and final positions to be able to compare
                            val standing = StandingService.toStanding(it)
                            Standing(standing, standing.numIntraGrpPos, null, null, standing.isIgpTied)
                        }
                        .sortedWith(compareBy(Standing::numIntraGrpPos))
                        .toSet()

                    assertThat(dbStandings).containsExactlyElementsOf(calcStandings)
                }
            }
    }

}