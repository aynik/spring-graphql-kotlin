rootProject.name = "spring-graphql-kotlin"

pluginManagement {
    val kotlinVersion: String by settings
    val ktlintVersion: String by settings
    val springPluginVersion: String by settings
    val springBootVersion: String by settings
    val graphqlKotlinVersion: String by settings
    val jacksonVersion: String by settings
    val postgresqlDriverVersion: String by settings
    val kobbyVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version springPluginVersion
        id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
        id("org.springframework.boot") version springBootVersion
        id("com.expediagroup.graphql") version graphqlKotlinVersion
        id("com.fasterxml.jackson.core") version jacksonVersion
        id("org.postgresql") version postgresqlDriverVersion
        id("io.github.ermadmi78.kobby") version kobbyVersion
    }
}
