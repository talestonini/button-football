package com.talestonini.service

import com.talestonini.model.MatchType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExposedMatchType(val id: Int, val code: String, val description: String)

class MatchTypeService(database: Database) : BaseService() {
    suspend fun read(code: String?): List<ExposedMatchType?> {
        return dbQuery {
            MatchType.all()
                .filter { if (code != null) it.code == code else true }
                .map { matchTypeMapper(it) }
        }
    }

    private fun matchTypeMapper(matchType: MatchType): ExposedMatchType =
        ExposedMatchType(
            matchType.id.value,
            matchType.code,
            matchType.description
        )
}
