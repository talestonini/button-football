package com.talestonini.service

import com.talestonini.model.MatchType
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

@Serializable
data class ExpMatchType(val id: Int, val code: String, val description: String)

class MatchTypeService(database: Database) : BaseService() {
    suspend fun read(code: String?): List<ExpMatchType?> {
        return dbQuery {
            MatchType.all()
                .filter { if (code != null) it.code == code else true }
                .map { toExpMatchType(it) }
        }
    }

    companion object {
        fun toExpMatchType(matchType: MatchType): ExpMatchType =
            ExpMatchType(
                matchType.id.value,
                matchType.code,
                matchType.description
            )
    }
}