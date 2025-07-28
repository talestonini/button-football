package com.talestonini.service.sorting

import com.talestonini.data.BaseDataTest
import com.talestonini.model.ChampionshipEntity
import com.talestonini.model.MatchEntity
import com.talestonini.model.StandingEntity
import com.talestonini.service.Constants
import com.talestonini.service.MatchService
import com.talestonini.service.Standing
import com.talestonini.service.StandingService
import com.talestonini.service.sorting.IntraGroupSortingService.Companion.processIntraGroupStandings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.fail
import kotlin.text.lowercase

class IntraGroupSortingServiceTest : BaseDataTest() {
    companion object {
        private const val RANDOMISATION_ATTEMPTS = 10
    }

    @Test
    fun `group standings should have teams in their correct intra-group position`() = dataTest {
        ChampionshipEntity.all().sortedBy { it.id.value }
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                (1..c.numTeams / Constants.NUM_TEAMS_PER_GROUP).forEach { g ->
                    println(">>> testing championship ${c.id.value} (${c.type.code}, ed. ${c.numEdition}) group g${g}")

                    val groupMatches = MatchEntity.all()
                        .filter { it.championship == c && it.type.code.lowercase() == "g$g" }
                        .map { MatchService.toMatch(it) }
                        .toSet()
                    val calcIntraGroupStandings = processIntraGroupStandings(groupMatches)

                    val dbStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code.lowercase() == "g$g" }
                        .map {
                            // must null the extra and final positions to be able to compare
                            val st = StandingService.toStanding(it)
                            Standing(
                                st, st.numIntraGrpPos, null, null, st.isIgpUntiedByHeadToHead,
                                st.isIgpUntiedRandomly
                            )
                        }
                        .sortedWith(compareBy(Standing::numIntraGrpPos))
                        .toSet()

                    if (calcIntraGroupStandings.all { !it.isIgpUntiedRandomly })
                        assertThat(dbStandings).containsExactlyElementsOf(calcIntraGroupStandings)
                    else
                    // test that, in at most N new standings calculations, the randomisation to
                    // break tied positions will produce the same standings of the database
                        for (i in 1..RANDOMISATION_ATTEMPTS)
                            try {
                                val calcStandingsAgain = processIntraGroupStandings(groupMatches)
                                assertThat(dbStandings).containsExactlyElementsOf(calcStandingsAgain)
                                break
                            } catch (e: AssertionError) {
                                println(">>> testing randomisation to break tied positions - attempt $i")
                                if (i == RANDOMISATION_ATTEMPTS) {
                                    println(e.message)
                                    fail(
                                        "randomisation did not produce alternative standings within " +
                                                "${RANDOMISATION_ATTEMPTS} attempts"
                                    )
                                }
                            }
                }
            }
    }

}