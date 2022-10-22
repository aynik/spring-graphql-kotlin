package com.graphql.dataloader

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.graphql.entity.User
import com.graphql.repository.UserRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserDataLoader(private val userRepository: UserRepository) : KotlinDataLoader<UUID, User> {
    companion object {
        const val name = "UserDataLoader"
    }

    override val dataLoaderName = name

    @OptIn(DelicateCoroutinesApi::class)
    override fun getDataLoader(): DataLoader<UUID, User> = DataLoaderFactory.newDataLoader { ids ->
        GlobalScope.future {
            newSuspendedTransaction {
                userRepository.fetch(ids = ids).map {
                    User.fromResultRow(it)
                }.associateBy { it.id }
            }.let { entityMap -> ids.map { entityMap[it] } }
        }
    }
}
