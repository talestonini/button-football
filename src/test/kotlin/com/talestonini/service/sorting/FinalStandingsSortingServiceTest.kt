package com.talestonini.service.sorting

import com.talestonini.data.BaseDataTest
import com.talestonini.model.ChampionshipEntity
import com.talestonini.model.MatchEntity
import com.talestonini.model.StandingEntity
import com.talestonini.service.MatchService
import com.talestonini.service.Standing
import com.talestonini.service.StandingService
import com.talestonini.service.sorting.FinalStandingsSortingService.Companion.processFinalStandings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.DefaultAsserter.fail
import kotlin.text.lowercase

class FinalStandingsSortingServiceTest : BaseDataTest() {
    companion object {
        private const val RANDOMISATION_ATTEMPTS = 10
    }

    @Test
    fun `championship final standings should have teams in their correct final position`() = dataTest {
        ChampionshipEntity.all().sortedBy { it.id.value }
            .filter { it.status.description.lowercase() == "encerrado" }
            .forEach { c ->
                println(">>> testing championship ${c.id.value} (${c.type.code}, ed. ${c.numEdition})")

                val groupStandings = StandingEntity.all()
                    .filter { it.championship == c && it.matchType.code.lowercase().startsWith("g") }
                    .map { StandingService.toStanding(it) }
                    .toSet()
                val finalsMatches = MatchEntity.all()
                    .filter { it.championship == c && !it.type.code.lowercase().startsWith("g") }
                    .map { MatchService.toMatch(it) }
                    .toSet()
                val calcFinalStandings = processFinalStandings(groupStandings, finalsMatches)

                val dbStandings = StandingEntity.all()
                    .filter { it.championship == c && !it.matchType.code.lowercase().startsWith("g") }
                    .map {
                        val st = StandingService.toStanding(it)
                        Standing(st, st.numIntraGrpPos, st.numExtraGrpPos, st.numFinalPos, st.isIgpUntiedByHeadToHead,
                            st.isIgpUntiedRandomly, st.isEgpUntiedRandomly, st.isFpUntiedRandomly)
                    }
                    .sortedWith(compareBy(Standing::numFinalPos))
                    .toSet()

                if (calcFinalStandings.all { !it.isFpUntiedRandomly })
                    assertThat(dbStandings).containsExactlyElementsOf(calcFinalStandings)
                else
                // test that, in at most N new standings calculations, the randomisation to
                // break tied positions will produce the same standings of the database
                    for (i in 1..RANDOMISATION_ATTEMPTS)
                        try {
                            val calcStandingsAgain = processFinalStandings(groupStandings, finalsMatches)
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