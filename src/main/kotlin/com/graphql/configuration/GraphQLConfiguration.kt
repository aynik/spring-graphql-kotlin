package com.graphql.configuration

import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistry
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.graphql.adapter.SchemaAdapter
import com.graphql.dataloader.UserDataLoader
import graphql.GraphQL
import graphql.kobby.kotlin.schemaContextOf
import graphql.schema.GraphQLSchema
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLConfiguration {

    @Bean
    fun hooks() = CustomSchemaGeneratorHooks()

    @Bean
    fun dataLoaderRegistry(userDataLoader: UserDataLoader) =
        KotlinDataLoaderRegistryFactory(userDataLoader).generate()

    @Bean("context")
    fun schemaContext(
        applicationContext: ApplicationContext,
        dataLoaderRegistry: KotlinDataLoaderRegistry,
        graphqlSchema: GraphQLSchema,
    ) = schemaContextOf(
        SchemaAdapter(
            dataLoaderRegistry,
            GraphQL.newGraphQL(graphqlSchema).build(),
            jacksonObjectMapper()
                .registerModule(ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
        ),
    )
}
