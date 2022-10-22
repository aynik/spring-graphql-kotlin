package com.graphql.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.graphql.entity.UserCollection
import com.graphql.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserQuery(
    private val userRepository: UserRepository,
) : Query {
    @GraphQLDescription("Retrieves users from repository")
    suspend fun users(
        limit: Int? = null,
        after: UUID? = null,
        reversed: Boolean? = null,
        ids: List<UUID>? = null,
        usernames: List<String>? = null,
        names: List<String>? = null,
        friendIds: List<UUID>? = null,
    ) = newSuspendedTransaction {
        UserCollection.fromQuery(
            limit = limit,
            after = after,
            reversed = reversed,
            query = userRepository.fetch(
                ids = ids,
                usernames = usernames,
                names = names,
                friendIds = friendIds,
            ),
        )
    }
}
