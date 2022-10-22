import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateSDLTask
import io.github.ermadmi78.kobby.task.KobbyKotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.springframework.boot")
    id("com.expediagroup.graphql")
    id("io.github.ermadmi78.kobby")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.panda-lang.org/releases") }
}

val group: String by project
val kotlinJvmVersion: String by project
val kotlinVersion: String by project
val ktlintVersion: String by project
val springBootVersion: String by project
val graphqlKotlinVersion: String by project
val kobbyVersion: String by project
val jacksonVersion: String by project
val postgresqlDriverVersion: String by project
val exposedVersion: String by project

dependencies {
    compileOnly("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresqlDriverVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-server:$graphqlKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-hooks-provider:$graphqlKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-schema-generator:$graphqlKotlinVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:$springBootVersion")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
}

application {
    mainClass.set("$group.Application")
}

configure<KtlintExtension> {
    filter {
        exclude { it.file.path.contains("/generated/") }
    }
}

graphql {
    schema {
        packages = listOf(group)
    }
}

kobby {
    kotlin {
        scalars = mapOf(
            "UUID" to typeOf("java.util", "UUID"),
        )
    }
}

val graphqlGenerateSDL by tasks.getting(GraphQLGenerateSDLTask::class) {
    schemaFile.set(file("${project.projectDir}/src/main/resources/graphql/schema.graphqls"))
}

val kobbyKotlin by tasks.getting(KobbyKotlin::class) {}

tasks.withType<KtLintCheckTask> {
    dependsOn(kobbyKotlin)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = kotlinJvmVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.build {
    finalizedBy(graphqlGenerateSDL)
}
