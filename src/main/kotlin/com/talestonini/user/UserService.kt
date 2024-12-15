package com.talestonini.user

import com.talestonini.service.BaseService
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(val name: String, val age: Int)

class UserService(database: Database) : BaseService() {
    init {
        transaction(database) {
            SchemaUtils.create(UsersTable)
        }
    }

    suspend fun create(user: ExposedUser): Int = dbQuery {
        UsersTable.insert {
            it[name] = user.name
            it[age] = user.age
        }[UsersTable.id]
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .map { ExposedUser(it[UsersTable.name], it[UsersTable.age]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: ExposedUser) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id }) {
                it[name] = user.name
                it[age] = user.age
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id.eq(id) }
        }
    }
}