package com.graphql.configuration

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import com.graphql.scalar.UUIDType
import graphql.schema.GraphQLType
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType

class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
        UUID::class -> UUIDType
        else -> null
    }
}

class CustomSchemaGeneratorHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks = CustomSchemaGeneratorHooks()
}
