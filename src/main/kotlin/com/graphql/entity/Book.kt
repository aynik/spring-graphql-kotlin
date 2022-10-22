package com.graphql.entity

import com.graphql.directives.Default
import com.graphql.repository.BookTable
import org.jetbrains.exposed.sql.ResultRow
import java.util.UUID

data class Book(
    @Default
    override val id: UUID,
    @Default
    val title: String,
) : Item {
    companion object {
        fun fromResultRow(row: ResultRow) = Book(
            id = row[BookTable.id],
            title = row[BookTable.title],
        )
    }
}
