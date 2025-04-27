package com.talestonini.service

import com.talestonini.model.MatchTypeEntity
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database

data class MatchType(val id: Int?, val code: String, val description: String) {
    constructor(code: String, description: String) : this(null, code, description)
}

@Serializable
data class MatchTypeApiView(val id: Int, val code: String, val description: String)

class MatchTypeService(database: Database) : BaseService() {

    companion object {
        fun toMatchType(matchTypeEntity: MatchTypeEntity): MatchType =
            MatchType(
                matchTypeEntity.code,
                matchTypeEntity.description
            )

        fun toMatchTypeApiView(matchTypeEntity: MatchTypeEntity): MatchTypeApiView =
            MatchTypeApiView(
                matchTypeEntity.id.value,
                matchTypeEntity.code,
                matchTypeEntity.description
            )
    }

    suspend fun read(code: String?): List<MatchTypeApiView?> {
        return dbQuery {
            MatchTypeEntity.all()
                .filter { if (code != null) it.code == code else true }
                .map { toMatchTypeApiView(it) }
        }
    }

}