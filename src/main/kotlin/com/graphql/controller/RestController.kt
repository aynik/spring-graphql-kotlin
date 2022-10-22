package com.graphql.controller

import com.graphql.repository.UserTable.id
import com.graphql.util.QueryUtil.allItems
import graphql.kobby.kotlin.SchemaContext
import graphql.kobby.kotlin.dto.BookInput
import graphql.kobby.kotlin.dto.ItemInput
import graphql.kobby.kotlin.dto.ItemType
import graphql.kobby.kotlin.dto.UserInput
import graphql.kobby.kotlin.entity.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class RestController(
    @Qualifier("context") private val schema: SchemaContext,
) {
    @GetMapping("/users")
    fun users(): Flow<String> {
        return allItems {
            schema.query {
                users(after = it, limit = 1) {
                    items { friends { items() } }
                }
            }.users.items
        }.zip(generateSequence { "\n" }.asFlow()) { first, second ->
            first.toString() + second
        }
    }

    @GetMapping("/add")
    suspend fun add(): Flow<String> {
        return merge(
            schema.mutation {
                users(
                    listOf(
                        UserInput(
                            id = UUID.fromString("bdd5f9f1-8abe-470c-8a80-8c7d6f070fef"),
                            username = "wayne",
                            name = "John Wayne",
                            friendIds = emptyList(),
                        ),
                        UserInput(
                            id = UUID.fromString("f00042be-ffcd-4254-8b91-3278b3af70ff"),
                            username = "mads",
                            name = "Mads Mikkelsen",
                            friendIds = emptyList(),
                        ),
                        UserInput(
                            id = UUID.fromString("fd417fd5-293b-4481-9228-5f8e35d35eb2"),
                            username = "peter",
                            name = "Peter Pan",
                            friendIds = emptyList(),
                        ),
                    ),
                )
            }.users.map { it.toString() }.asFlow(),
            schema.mutation {
                books(
                    listOf(
                        BookInput(
                            id = UUID.fromString("bdd5f9f1-8abe-470c-8a80-8c7d6f070f34"),
                            title = "The Great Gatsby",
                        ),
                        BookInput(
                            id = UUID.fromString("bdd5f9f1-8abe-470c-8a80-8c7d6f070f67"),
                            title = "Moby Dick",
                        ),
                    ),
                )
            }.books.map { it.toString() }.asFlow(),
        ).zip(generateSequence { "\n" }.asFlow()) { first, second ->
            first + second
        }
    }

    @GetMapping("/delete")
    suspend fun delete(): Flow<String> {
        return schema.mutation {
            delete(
                listOf(
                    ItemInput(id = UUID.fromString("bdd5f9f1-8abe-470c-8a80-8c7d6f070fef"), __typename = ItemType.User),
                    ItemInput(id = UUID.fromString("f00042be-ffcd-4254-8b91-3278b3af70ff"), __typename = ItemType.User),
                    ItemInput(id = UUID.fromString("fd417fd5-293b-4481-9228-5f8e35d35eb2"), __typename = ItemType.User),
                    ItemInput(id = UUID.fromString("bdd5f9f1-8abe-470c-8a80-8c7d6f070f34"), __typename = ItemType.Book),
                    ItemInput(id = UUID.fromString("bdd5f9f1-8abe-470c-8a80-8c7d6f070f67"), __typename = ItemType.Book),
                ),
            )
        }.delete.asFlow().zip(generateSequence { "\n" }.asFlow()) { first, second ->
            first.toString() + second
        }
    }
}
