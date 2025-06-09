package com.talestonini.data

import com.talestonini.model.*
import com.talestonini.service.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.fail

class GroupStandingsTest : BaseDataTest() {

    companion object {
        private const val RANDOMISATION_ATTEMPTS = 10

        private val EGP_HISTORICAL_MISTAKES = setOf(
            // Necaxa was let down (extra-group position 9) in favour of Palmeiras (extra-group position 8):
            "but some elements were not found:\n" +
                    "  [ch=la, ed=2, type=g3, team=Necaxa, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, " +
                    "eg=8, egUntiedRandomly=false, fp=null,\n" +
                    "    ch=la, ed=2, type=g1, team=Palmeiras, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, " +
                    "eg=9, egUntiedRandomly=false, fp=null]\n" +
                    "and others were not expected:\n" +
                    "  [ch=la, ed=2, type=g1, team=Palmeiras, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, " +
                    "eg=8, egUntiedRandomly=false, fp=null,\n" +
                    "    ch=la, ed=2, type=g3, team=Necaxa, ig=3, igUntiedByH2H=false, igUntiedRandomly=false, " +
                    "eg=9, egUntiedRandomly=false, fp=null]"
        )
    }

    @Test
    fun `group standings should have teams in their correct intra-group position`() = dataTest {
        ChampionshipEntity.all()
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                (1..c.numTeams/Constants.NUM_TEAMS_PER_GROUP).forEach { g ->
                    println(">>> testing championship ${c.id.value} (${c.type.code}, ed. ${c.numEdition}) group g${g}")

                    val matches = MatchEntity.all()
                        .filter { it.championship == c && it.type.code == "g$g" }
                        .map { MatchService.toMatch(it) }
                        .toSet()
                    val calcStandings = ManagementService.processIntraGroupStandings(matches)

                    val dbStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code == "g$g" }
                        .map {
                            // must null the extra and final positions to be able to compare
                            val st = StandingService.toStanding(it)
                            Standing(st, st.numIntraGrpPos, null, null, st.isIgpUntiedByHeadToHead,
                                st.isIgpUntiedRandomly)
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
                                val calcStandingsAgain = ManagementService.processIntraGroupStandings(matches)
                                assertThat(dbStandings).containsExactlyElementsOf(calcStandingsAgain)
                                break
                            } catch (e: AssertionError) {
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

    @Test
    fun `group standings should have teams in their correct extra-group position`() = dataTest {
        ChampionshipEntity.all()
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                (1..c.numTeams/Constants.NUM_TEAMS_PER_GROUP).forEach { g ->
                    println(">>> testing championship ${c.id.value} (${c.type.code}, ed. ${c.numEdition})")

                    val standings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code.startsWith("g") }
                        .map { StandingService.toStanding(it) }
                        .toSet()
                    val calcStandings = ManagementService.processExtraGroupStandings(standings)

                    val dbStandings = StandingEntity.all()
                        .filter { it.championship == c && it.matchType.code.startsWith("g") }
                        .map {
                            // must null the final positions to be able to compare
                            val st = StandingService.toStanding(it)
                            Standing(st, st.numIntraGrpPos, st.numExtraGrpPos, null, st.isIgpUntiedByHeadToHead,
                                st.isIgpUntiedRandomly, st.isEgpUntiedRandomly)
                        }
                        .sortedWith(compareBy(Standing::numExtraGrpPos))
                        .toSet()

                    if (calcStandings.all { !it.isEgpUntiedRandomly })
                        assertThat(dbStandings).containsExactlyElementsOf(calcStandings)
                    else
                        // test that, in at most x new standings calculations, the randomisation to
                        // break tied positions will produce the same standings of the database
                        for (i in 1..RANDOMISATION_ATTEMPTS)
                            try {
                                val calcStandingsAgain = ManagementService.processExtraGroupStandings(standings)
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

    @Test
    fun `championship final standings should have teams in their correct final position`() = dataTest {
        ChampionshipEntity.all()
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
            }
    }

}