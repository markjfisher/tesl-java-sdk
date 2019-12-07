import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    id("com.google.cloud.tools.jib")
}

group = "net.markjfisher"
version = "1.0.2"
val teslArchiveBaseName = "tesl-java-bot"

val konfigVersion: String by project
val fuzzyMatchVersion: String by project
val diskordVersion: String by project

val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

val assertJVersion: String by project
val mockkVersion: String by project
val junitJupiterEngineVersion: String by project

// Supply the image url in ~/.gradle/gradle.properties
val botDockerImageURL: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("com.natpryce:konfig:$konfigVersion")

    implementation(group = "com.jessecorbett", name = "diskord-jvm", version = diskordVersion)
    implementation(group = "me.xdrop", name = "fuzzywuzzy", version = fuzzyMatchVersion)

    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    implementation(project(":sdk"))
}

tasks {
    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

    named<Jar>("jar") {
        archiveBaseName.set(teslArchiveBaseName)
    }

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    register<Jar>("javadocJar") {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }

    javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    named<JavaExec>("run") {
        jvmArgs(listOf("-noverify", "-XX:TieredStopAtLevel=1"))
    }

}

application {
    mainClassName = "tesl.bot.BotCheck"
}

jib {
    from {
        image = "registry://adoptopenjdk/openjdk11"
    }
    container {
        jvmFlags = listOf("-Xms256m", "-Xmx256m")
        mainClass = "tesl.bot.BotCheck"
        args = listOf("")
        // ports = listOf("80")
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
    to {
        // Docker Hub - credentials needed. See CREDENTIALS_DOCKER.md
        image = botDockerImageURL
        credHelper = "secretservice"
        tags = setOf("latest")
        // Can add a specific version to deploy to as follows:
        // tags = setOf("latest", "${project.version}")
    }
}