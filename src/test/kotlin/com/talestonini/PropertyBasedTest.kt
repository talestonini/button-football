package com.talestonini

import com.talestonini.model.MatchType
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Provide
import net.jqwik.kotlin.api.any
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

abstract class PropertyBasedTest {

    private fun initDatabase() =
        Database.connect(
            url = "jdbc:h2:./h2/db/buttonfootball",
            driver = "org.h2.Driver",
            user = "sa",
            password = "buttonfootball"
        )

    private fun <T> arbitrariesOf(fn: () -> List<T>): Arbitrary<T> {
        initDatabase()
        var res: Arbitrary<T> = Arbitraries.of()
        transaction {
            res = Arbitraries.of(fn())
        }
        return res
    }

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