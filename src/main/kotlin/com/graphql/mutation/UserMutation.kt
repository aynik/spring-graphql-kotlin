package com.graphql.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import com.graphql.entity.User
import com.graphql.repository.UserRepository
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component

@Component
class UserMutation(private val userRepository: UserRepository) : Mutation {

    @GraphQLDescription("Mutate users")
    suspend fun users(users: List<User>) = newSuspendedTransaction {
        userRepository.upsert(users).map { User.fromResultRow(it) }
    }
}
