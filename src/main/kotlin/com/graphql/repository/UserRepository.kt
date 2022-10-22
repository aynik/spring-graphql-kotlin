package com.graphql.repository

import com.graphql.column.array
import com.graphql.column.containsAny
import com.graphql.entity.User
import com.graphql.extension.batchUpsert
import com.graphql.repository.UserTable.friendIds
import com.graphql.repository.UserTable.id
import com.graphql.repository.UserTable.name
import com.graphql.repository.UserTable.username
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.UUIDColumnType
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Component
import java.util.UUID

object UserTable : Table("users") {
    val id = uuid("id")
    val username = varchar("username", 50)
    val name = text("name")
    val friendIds = array<UUID>("friend_ids", UUIDColumnType())

    override val primaryKey = PrimaryKey(id)
}

@Component
class UserRepository {
    suspend fun fetch(
        ids: List<UUID>? = null,
        usernames: List<String>? = null,
        names: List<String>? = null,
        friendIds: List<UUID>? = null,
    ) = UserTable.select { Op.TRUE }.apply {
        if (ids != null) andWhere { id inList ids }
        if (usernames != null) andWhere { username inList usernames }
        if (names != null) andWhere { name inList names }
        if (friendIds != null) andWhere { UserTable.friendIds containsAny friendIds.toTypedArray() }
    }

    suspend fun upsert(users: List<User>? = null) =
        users?.let {
            UserTable.batchUpsert(users) { table, item ->
                table[id] = item.id
                table[username] = item.username
                table[name] = item.name
                table[friendIds] = item.friendIds.toTypedArray()
            }.resultedValues
        } ?: emptyList()

    suspend fun delete(ids: List<UUID>? = null) = ids?.also {
        UserTable.deleteWhere { id inList ids }
        UserTable.select { friendIds containsAny ids.toTypedArray() }
            .map { row ->
                UserTable.update({ id eq row[id] }) {
                    it[friendIds] = row[friendIds].toMutableList()
                        .apply { removeAll(ids) }.toTypedArray()
                }
            }
    }
}
