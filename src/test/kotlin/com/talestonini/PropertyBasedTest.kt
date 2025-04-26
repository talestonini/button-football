package com.talestonini

import com.talestonini.model.MatchTypeEntity
import com.talestonini.service.MatchType
import com.talestonini.service.MatchTypeService
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import org.jetbrains.exposed.sql.transactions.transaction

abstract class PropertyBasedTest : BaseTest() {

    private fun <T> arbitrariesFromDb(dbQuery: () -> List<T>): Arbitrary<T> {
        database()
        var res = emptyList<T>()
        transaction {
            res = dbQuery()
        }
        return Arbitraries.of(res)
    }

    @Provide
    fun matchTypes(): Arbitrary<MatchType> =
        arbitrariesFromDb {
            MatchTypeEntity.all()
                .map { MatchTypeService.toMatchType(it) }
        }

    @Provide
    fun groupStageMatchTypes(): Arbitrary<MatchType> =
        arbitrariesFromDb {
            MatchTypeEntity.all()
                .filter { it.code.lowercase().startsWith("g") }
                .map { MatchTypeService.toMatchType(it) }
        }

    @Provide
    fun finalsMatchTypes(): Arbitrary<MatchType> =
        arbitrariesFromDb {
            MatchTypeEntity.all()
                .filter { !it.code.lowercase().startsWith("g") }
                .map { MatchTypeService.toMatchType(it) }
        }

    @Provide
    fun scores(): Arbitrary<Int> =
        Int.any().greaterOrEqual(0).lessOrEqual(10)

}