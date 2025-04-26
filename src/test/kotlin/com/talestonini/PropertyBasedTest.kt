package com.talestonini

import com.talestonini.model.MatchType
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import org.jetbrains.exposed.sql.transactions.transaction

abstract class PropertyBasedTest : BaseTest() {

    private fun <T> arbitrariesFromDb(fn: () -> List<T>): Arbitrary<T> {
        database()
        var res = emptyList<T>()
        transaction {
            res = fn()
        }
        return Arbitraries.of(res)
    }

    @Provide
    fun matchTypes(): Arbitrary<String> =
        arbitrariesFromDb {
            MatchType.all().map { it.description }
        }

    @Provide
    fun groupStageMatchTypes(): Arbitrary<String> =
        arbitrariesFromDb {
            MatchType.all().filter { it.code.lowercase().startsWith("g") }.map { it.description }
        }

    @Provide
    fun finalsMatchTypes(): Arbitrary<String> =
        arbitrariesFromDb {
            MatchType.all().filter { !it.code.lowercase().startsWith("g") }.map { it.description }
        }

    @Provide
    fun scores(): Arbitrary<Int> =
        Int.any().greaterOrEqual(0).lessOrEqual(10)

}