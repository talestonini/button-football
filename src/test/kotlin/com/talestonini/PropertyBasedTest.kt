package com.talestonini

import com.talestonini.model.MatchType
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any

abstract class PropertyBasedTest : BaseTest() {

    private fun <T> arbitrariesOf(fn: () -> List<T>): Arbitrary<T> =
        Arbitraries.of(fromDb { fn() })

    @Provide
    fun matchTypes(): Arbitrary<String> =
        arbitrariesOf {
            MatchType.all().map { it.description }
        }

    @Provide
    fun groupStageMatchTypes(): Arbitrary<String> =
        arbitrariesOf {
            MatchType.all().filter { it.code.lowercase().startsWith("g") }.map { it.description }
        }

    @Provide
    fun finalsMatchTypes(): Arbitrary<String> =
        arbitrariesOf {
            MatchType.all().filter { !it.code.lowercase().startsWith("g") }.map { it.description }
        }

    @Provide
    fun scores(): Arbitrary<Int> =
        Int.any().greaterOrEqual(0).lessOrEqual(10)

}