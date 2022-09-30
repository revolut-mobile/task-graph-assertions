import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.gradle.plugin-publish") version "1.0.0"
}

group = "com.revolut"
version = "0.3"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.google.truth:truth:1.1.3")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    explicitApi()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

gradlePlugin {
    plugins {
        create("task-graph-assertions") {
            id = "com.revolut.task-graph-assertions"
            implementationClass = "com.revolut.TaskGraphAssertionsPlugin"
            displayName = "Task Graph Assertions Plugin"
            description = "Add assertions to be aware that your complex gradle build tasks hierarchy is still here"
        }
    }
}

pluginBundle {
    website = "https://github.com/revolut-mobile/task-graph-assertions#readme"
    vcsUrl = "https://github.com/revolut-mobile/task-graph-assertions"

    tags = listOf("validation", "task-graph")
}
