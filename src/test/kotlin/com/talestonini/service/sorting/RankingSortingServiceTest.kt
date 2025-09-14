package com.talestonini.service.sorting

import com.talestonini.data.BaseDataTest
import com.talestonini.model.ChampionshipTypeEntity
import com.talestonini.model.RankingEntity
import com.talestonini.service.Ranking
import com.talestonini.service.RankingService
import com.talestonini.service.sorting.RankingSortingService.Companion.processRanking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RankingSortingServiceTest : BaseDataTest() {

    @Test
    fun `ranking should have teams in their correct position`() = dataTest {
        ChampionshipTypeEntity.all().sortedBy { it.id.value }
            .forEach { ct ->
                for (i in 0..ct.numEditions) {
                    println(">>> testing championship type '${ct.code}', up to edition $i")

                    val calcRanking = processRanking(ct.code, i)

                    val dbRanking = RankingEntity.all()
                        .filter { it.championshipType == ct && it.numUpToEdition == i }
                        .map { RankingService.toRanking(it) }
                        .sortedWith(compareBy(Ranking::numRankingPos))
                        .toSet()

                    assertThat(dbRanking).containsExactlyElementsOf(calcRanking)
                }
            }
    }

}