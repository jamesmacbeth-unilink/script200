import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
}
group = "com.unilink"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "atlassian-public"
        url = uri("https://packages.atlassian.com/maven/repository/public")
    }
}
dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation("com.atlassian.jira:jira-rest-java-client-core:4.0.0")
    implementation("com.atlassian.fugue:fugue:2.6.1")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
