package com.graphql.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.graphql.entity.Book
import com.graphql.entity.ItemInput
import com.graphql.entity.ItemType
import com.graphql.entity.User
import com.graphql.repository.BookRepository
import com.graphql.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component

@Component
class GenericMutation(
    private val userRepository: UserRepository,
    private val bookRepository: BookRepository,
) : Mutation {

    @GraphQLDescription("Delete items")
    suspend fun delete(items: List<ItemInput>) = newSuspendedTransaction {
        items.groupBy { it.__typename }.entries.flatMap { (itemType, items) ->
            when (itemType) {
                ItemType.User -> userRepository.fetch(ids = items.map { it.id })
                    .map { User.fromResultRow(it) }
                    .also { users -> userRepository.delete(users.map { it.id }) }

                ItemType.Book -> bookRepository.fetch(ids = items.map { it.id })
                    .map { Book.fromResultRow(it) }
                    .also { books -> bookRepository.delete(books.map { it.id }) }
            }
        }
    }
}
