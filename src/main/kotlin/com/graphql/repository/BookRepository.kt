package com.graphql.repository

import com.graphql.entity.Book
import com.graphql.extension.batchUpsert
import com.graphql.repository.BookTable.id
import com.graphql.repository.BookTable.title
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component
import java.util.UUID

object BookTable : Table("books") {
    val id = uuid("id")
    val title = text("title")

    override val primaryKey = PrimaryKey(id)
}

@Component
class BookRepository {
    suspend fun fetch(
        ids: List<UUID>? = null,
        titles: List<String>? = null,
    ) = BookTable.select { Op.TRUE }.apply {
        if (ids != null) andWhere { id inList ids }
        if (titles != null) andWhere { title inList titles }
    }

    suspend fun upsert(books: List<Book>? = null) =
        books?.let {
            BookTable.batchUpsert(books) { table, item ->
                table[id] = item.id
                table[title] = item.title
            }.resultedValues
        } ?: emptyList()

    suspend fun delete(ids: List<UUID>? = null) = ids?.also {
        BookTable.deleteWhere { id inList ids }
    }
}
