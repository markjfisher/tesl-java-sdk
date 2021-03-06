//import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("com.github.johnrengelman.shadow")
}

val uuidGeneratorVersion: String by project
val jacksonVersion: String by project
val unirestJavaVersion: String by project
val konfigVersion: String by project
val cacheVersion: String by project
val jsoupVersion: String by project
val kranglVersion: String by project
val argParserVersion: String by project

val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

val assertJVersion: String by project
val mockkVersion: String by project
val junitJupiterEngineVersion: String by project
val testContainersVersion: String by project

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.httpcomponents:httpclient-cache:$cacheVersion")
    implementation("com.konghq:unirest-java:$unirestJavaVersion") {
        exclude(group = "org.apache.httpcomponents", module = "httpclient-cache")
    }
    implementation("org.jsoup:jsoup:$jsoupVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("com.fasterxml.uuid:java-uuid-generator:$uuidGeneratorVersion")
    implementation("com.natpryce:konfig:$konfigVersion")
    implementation("org.apache.httpcomponents:httpclient-cache:$cacheVersion")
    implementation("de.mpicbg.scicomp:krangl:$kranglVersion")
    implementation("com.xenomachina:kotlin-argparser:$argParserVersion")

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

    register("analyseLogs", JavaExec::class) {
        description = "Analyse log file for deck codes and output some stats about them"
        main = "tesl.stats.LogAnalyser"
        classpath = sourceSets["main"].runtimeClasspath
        if (project.hasProperty("args")) {
            args = (project.findProperty("args") as String).split(" ")
        }
    }
}
