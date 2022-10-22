package com.graphql.entity

import com.graphql.directives.Default
import java.util.UUID

enum class ItemType {
    User,
    Book
}

interface Item {
    @Default
    val id: UUID
}

data class ItemInput(
    override val id: UUID,
    val __typename: ItemType,
) : Item
