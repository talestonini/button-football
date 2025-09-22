package com.talestonini.service.sorting

import com.talestonini.data.BaseDataTest
import com.talestonini.model.ChampionshipEntity
import com.talestonini.model.ChampionshipTypeEntity
import com.talestonini.model.RankingEntity
import com.talestonini.model.StandingEntity
import com.talestonini.service.ChampionshipTypeService
import com.talestonini.service.Ranking
import com.talestonini.service.RankingService
import com.talestonini.service.ScoringService
import com.talestonini.service.StandingService
import com.talestonini.service.sorting.RankingSortingService.Companion.processRanking
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RankingSortingServiceTest : BaseDataTest() {

    val DECIMAL_PLACES_FOR_COMPARISON = 5

    @Test
    fun `ranking should have teams in their correct position`() = dataTest {
        ChampionshipTypeEntity.all().sortedBy { it.id.value }
            .forEach { ct ->
                val numFinishedEditions = ChampionshipEntity.all()
                    .count { it.type == ct && it.status.description.lowercase() != "encerrado" }
                for (i in 1..numFinishedEditions) {
                    println(">>> testing championship type '${ct.code}', up to edition $i")

                    val finalStandings = StandingEntity.all()
                        .filter {
                            it.championship.type == ct &&
                                    it.championship.numEdition <= i &&
                                    it.matchType.code.lowercase() == "f1"
                        }
                        .map { StandingService.toStanding(it) }
                        .toSet()

                    val scoring = runBlocking {
                        ScoringService().championshipTypeScoring(ChampionshipTypeService.toChampionshipType(ct))
                    }

                    val calcRanking = processRanking(finalStandings, scoring)
                        .map { Ranking(toClone = it, numAvgPos = it.numAvgPos.roundTo(DECIMAL_PLACES_FOR_COMPARISON)) }

                    val dbRanking = RankingEntity.all()
                        .filter { it.championshipType == ct && it.numUpToEdition == i }
                        .map { Ranking(
                            toClone = RankingService.toRanking(it),
                            numAvgPos = it.numAvgPos.roundTo(DECIMAL_PLACES_FOR_COMPARISON))
                        }
                        .sortedWith(compareBy(Ranking::numRankingPos))
                        .toSet()

                    assertThat(dbRanking).containsExactlyElementsOf(calcRanking)
                }
            }
    }

}