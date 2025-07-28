package com.talestonini.service.sorting

import com.talestonini.data.BaseDataTest
import com.talestonini.model.ChampionshipEntity
import com.talestonini.model.StandingEntity
import com.talestonini.service.Constants
import com.talestonini.service.Standing
import com.talestonini.service.StandingService
import com.talestonini.service.sorting.ExtraGroupSortingService.Companion.processExtraGroupStandings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.fail
import kotlin.text.lowercase

class ExtraGroupSortingServiceTest : BaseDataTest() {
    companion object {
        private const val RANDOMISATION_ATTEMPTS = 10

        private val EGP_HISTORICAL_MISTAKES = setOf(
            // Necaxa was let down (extra-group position 9) in favour of Palmeiras (extra-group position 8):
            """but some elements were not found:
  [ch=la, ed=2, team=Necaxa, type=g3, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, eg=8, egUntiedRandomly=false, fp=null, fpUntiedRandomly=false, wins=1, draws=0, losses=2, gs=4, gc=8, gd=-4,
    ch=la, ed=2, team=Palmeiras, type=g1, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, eg=9, egUntiedRandomly=false, fp=null, fpUntiedRandomly=false, wins=0, draws=3, losses=0, gs=3, gc=3, gd=0]
and others were not expected:
  [ch=la, ed=2, team=Palmeiras, type=g1, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, eg=8, egUntiedRandomly=false, fp=null, fpUntiedRandomly=false, wins=0, draws=3, losses=0, gs=3, gc=3, gd=0,
    ch=la, ed=2, team=Necaxa, type=g3, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, eg=9, egUntiedRandomly=false, fp=null, fpUntiedRandomly=false, wins=1, draws=0, losses=2, gs=4, gc=8, gd=-4]"""
        )
    }

    @Test
    fun `group standings should have teams in their correct extra-group position`() = dataTest {
        ChampionshipEntity.all().sortedBy { it.id.value }
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                (1..c.numTeams/Constants.NUM_TEAMS_PER_GROUP).forEach { g ->
                    println(">>> testing championship ${c.id.value} (${c.type.code}, ed. ${c.numEdition})")

                    val groupStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code.lowercase().startsWith("g") }
                        .map { StandingService.toStanding(it) }
                        .toSet()
                    val calcExtraGroupStandings = processExtraGroupStandings(groupStandings)

                    val dbStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code.lowercase().startsWith("g") }
                        .map {
                            // must null the final positions to be able to compare
                            val st = StandingService.toStanding(it)
                            Standing(st, st.numIntraGrpPos, st.numExtraGrpPos, null, st.isIgpUntiedByHeadToHead,
                                st.isIgpUntiedRandomly, st.isEgpUntiedRandomly)
                        }
                        .sortedWith(compareBy(Standing::numExtraGrpPos))
                        .toSet()

                    if (calcExtraGroupStandings.all { !it.isEgpUntiedRandomly })
                        assertThat(dbStandings).containsExactlyElementsOf(calcExtraGroupStandings)
                    else
                    // test that, in at most N new standings calculations, the randomisation to
                    // break tied positions will produce the same standings of the database
                        for (i in 1..RANDOMISATION_ATTEMPTS)
                            try {
                                val calcStandingsAgain = processExtraGroupStandings(groupStandings)
                                assertThat(dbStandings).containsExactlyElementsOf(calcStandingsAgain)
                                break
                            } catch (e: AssertionError) {
                                if (EGP_HISTORICAL_MISTAKES.any { e.message!!.contains(it) }) break

                                println(">>> testing randomisation to break tied positions - attempt $i")
                                if (i == RANDOMISATION_ATTEMPTS) {
                                    println(e.message)
                                    fail("randomisation did not produce alternative standings within " +
                                            "$RANDOMISATION_ATTEMPTS attempts")
                                }
                            }
                }
            }
    }

}