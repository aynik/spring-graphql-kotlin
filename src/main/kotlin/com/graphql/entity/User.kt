package com.graphql.entity

import com.expediagroup.graphql.server.extensions.getValuesFromDataLoader
import com.graphql.dataloader.UserDataLoader
import com.graphql.directives.Default
import com.graphql.repository.UserTable
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import java.util.UUID

data class User(
    @Default
    override val id: UUID,
    @Default
    val name: String,
    @Default
    val username: String,
    @Default
    val friendIds: List<UUID>,
) : Item {
    companion object {
        fun fromResultRow(row: ResultRow) = User(
            id = row[UserTable.id],
            username = row[UserTable.username],
            name = row[UserTable.name],
            friendIds = row[UserTable.friendIds].toList(),
        )
    }

    fun friends(
        dfe: DataFetchingEnvironment,
        limit: Int? = null,
        after: UUID? = null,
        reversed: Boolean? = null,
    ) = dfe.getValuesFromDataLoader<UUID, User>(
        UserDataLoader.name,
        friendIds
            .let { if (reversed == true) it.reversed() else it }
            .let { if (after != null) it.drop(it.lastIndexOf(after) + 1) else it }
            .let { if (limit != null) it.take(limit) else it },
    ).thenApply {
        UserCollection(
            items = it,
            totalCount = friendIds.size,
        )
    }
}

data class UserCollection(
    val totalCount: Int,
    val items: List<User>,
) {
    companion object {
        private const val DEFAULT_FETCH_ITEM_COUNT = 50

        fun fromQuery(
            query: Query,
            limit: Int? = null,
            after: UUID? = null,
            reversed: Boolean? = null,
        ) = UserCollection(
            totalCount = query.copy().count().toInt(),
            items = query.copy()
                .orderBy(UserTable.id, if (reversed == true) SortOrder.DESC else SortOrder.ASC)
                .apply {
                    if (after != null) {
                        if (reversed == true) andWhere { UserTable.id less after }
                        else andWhere { UserTable.id greater after }
                    }
                }
                .limit(limit ?: DEFAULT_FETCH_ITEM_COUNT)
                .map { User.fromResultRow(it) },
        )
    }
}
