package com.graphql.adapter

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.server.extensions.toGraphQLResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.kobby.kotlin.SchemaAdapter
import graphql.kobby.kotlin.dto.graphql.SchemaException
import graphql.kobby.kotlin.dto.graphql.SchemaMutationResult
import graphql.kobby.kotlin.dto.graphql.SchemaQueryResult
import graphql.kobby.kotlin.dto.graphql.SchemaRequest

class SchemaAdapter(
    private val dataLoaderRegistry: KotlinDataLoaderRegistry,
    private val graphql: GraphQL,
    private val mapper: ObjectMapper,
) : SchemaAdapter {
    override suspend fun executeQuery(query: String, variables: Map<String, Any?>) = graphql
        .execute(
            ExecutionInput
                .newExecutionInput()
                .dataLoaderRegistry(dataLoaderRegistry)
                .query(query)
                .variables(mapper.convertValue(variables, object : TypeReference<Map<String, Any>>() {}))
                .build(),
        )
        .toGraphQLResponse()
        .let { mapper.convertValue(it, SchemaQueryResult::class.java) }
        .let { result ->
            result.errors?.takeIf { it.isNotEmpty() }?.let {
                throw SchemaException("GraphQL query failed", SchemaRequest(query, variables), it)
            }
            result.data ?: throw SchemaException(
                "GraphQL query completes successfully but returns no data",
                SchemaRequest(query, variables),
            )
        }

    override suspend fun executeMutation(query: String, variables: Map<String, Any?>) = graphql
        .execute(
            ExecutionInput
                .newExecutionInput()
                .dataLoaderRegistry(dataLoaderRegistry)
                .query(query)
                .variables(mapper.convertValue(variables, object : TypeReference<Map<String, Any>>() {}))
                .build(),
        )
        .toGraphQLResponse()
        .let { mapper.convertValue(it, SchemaMutationResult::class.java) }
        .let { result ->
            result.errors?.takeIf { it.isNotEmpty() }?.let {
                throw SchemaException("GraphQL query failed", SchemaRequest(query, variables), it)
            }
            result.data ?: throw SchemaException(
                "GraphQL query completes successfully but returns no data",
                SchemaRequest(query, variables),
            )
        }
}
