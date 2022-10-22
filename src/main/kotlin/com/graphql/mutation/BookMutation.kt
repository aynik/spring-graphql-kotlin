package com.graphql.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.graphql.entity.Book
import com.graphql.repository.BookRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component

@Component
class BookMutation(private val bookRepository: BookRepository) : Mutation {

    @GraphQLDescription("Mutate books")
    suspend fun books(books: List<Book>) = newSuspendedTransaction {
        bookRepository.upsert(books).map { Book.fromResultRow(it) }
    }
}
