package com.graphql.util

import graphql.kobby.kotlin.entity.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

object QueryUtil {
    fun allItems(execute: suspend (afterId: UUID?) -> List<Item>): Flow<Item> = flow {
        var afterId: UUID? = null
        do {
            val itemList = execute(afterId).onEach { emit(it) }.also { afterId = it.lastOrNull()?.id }
        } while (itemList.isNotEmpty())
    }.flowOn(Dispatchers.IO)
}
