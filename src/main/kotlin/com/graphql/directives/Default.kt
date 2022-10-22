package com.graphql.directives

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation.FIELD_DEFINITION
import graphql.introspection.Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION

@GraphQLDirective(
    name = "default",
    description = "This field is selected by default",
    locations = [FIELD_DEFINITION, INPUT_FIELD_DEFINITION]
)
annotation class Default
