package com.talestonini.data

import com.talestonini.model.*
import com.talestonini.service.ManagementService
import com.talestonini.service.MatchService
import com.talestonini.service.Standing
import com.talestonini.service.StandingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.fail

class GroupStandingsTest : BaseDataTest() {

    companion object {
        private const val RANDOMISATION_ATTEMPTS = 5
    }

    @Test
    fun `group standings should have group teams in their correct intra-group position`() = dataTest {
        ChampionshipEntity.all()
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                (1..c.numTeams/4).forEach { g ->
                    println(">>> testing championship ${c.id.value} (${c.type.code}, ed. ${c.numEdition}) group g${g}")

                    val matches = MatchEntity.all()
                        .filter { it.championship == c && it.type.code == "g$g" }
                        .map { MatchService.toMatch(it) }
                        .toSet()
                    val calcStandings = ManagementService.processGroupStandings(matches)

                    val dbStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code == "g$g" }
                        .map {
                            // must null the extra and final positions to be able to compare
                            val standing = StandingService.toStanding(it)
                            Standing(standing, standing.numIntraGrpPos, null, null, standing.isIgpUntiedByHeadToHead,
                                standing.isIgpUntiedRandomly)
                        }
                        .sortedWith(compareBy(Standing::numIntraGrpPos))
                        .toSet()

                    if (calcStandings.all { !it.isIgpUntiedRandomly })
                        assertThat(dbStandings).containsExactlyElementsOf(calcStandings)
                    else
                        // test that, in at most x new standings calculations, the randomisation to
                        // break tied positions will produce the same standings of the database
                        for (i in 1..RANDOMISATION_ATTEMPTS)
                            try {
                                val calcStandingsAgain = ManagementService.processGroupStandings(matches)
                                assertThat(dbStandings).containsExactlyElementsOf(calcStandingsAgain)
                                break
                            } catch (e: AssertionError) {
                                println(">>> testing randomisation to break tied positions - attempt $i")
                                if (i == RANDOMISATION_ATTEMPTS)
                                    fail("randomisation did not produce alternative standings within " +
                                            "$RANDOMISATION_ATTEMPTS attempts")
                            }
                }
            }
    }

}