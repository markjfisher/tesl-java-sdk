import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    id("com.github.johnrengelman.shadow")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
    `java-library`
    `maven-publish`
    signing
}

group = "net.markjfisher"
version = "1.0.16"
val teslArchiveBaseName = "tesl-java-rest"

val micronautBoMVersion: String by project

dependencyManagement {
    imports {
        mavenBom("io.micronaut:micronaut-bom:$micronautBoMVersion")
    }
}

val sonatypeUsername: String by project
val sonatypePassword: String by project

val uuidGeneratorVersion: String by project
val jacksonVersion: String by project
val konfigVersion: String by project
val classgraphVersion: String by project
val fuzzyMatchVersion: String by project

val diskordVersion: String by project

val micronautJunit: String by project
val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

val assertJVersion: String by project
val mockkVersion: String by project
val junitJupiterEngineVersion: String by project

// Supply the image url in ~/.gradle/gradle.properties
val restDockerImageURL: String by project

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-discovery-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.configuration:micronaut-micrometer-core")
    implementation("io.micronaut.configuration:micronaut-micrometer-registry-prometheus")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut:micronaut-security")
    implementation("io.micronaut.configuration:micronaut-hibernate-validator")

    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")

    kaptTest("io.micronaut:micronaut-inject-java")

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("com.fasterxml.uuid:java-uuid-generator:$uuidGeneratorVersion")
    implementation("com.natpryce:konfig:$konfigVersion")
    implementation("io.github.classgraph:classgraph:$classgraphVersion")

    implementation(group = "com.jessecorbett", name = "diskord-jvm", version = diskordVersion)
    implementation(group = "me.xdrop", name = "fuzzywuzzy", version = fuzzyMatchVersion)

    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.micronaut.test:micronaut-test-junit5:$micronautJunit")

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

    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
    }

    named<JavaExec>("run") {
        jvmArgs(listOf("-noverify", "-XX:TieredStopAtLevel=1"))
    }

}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            artifactId = teslArchiveBaseName
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("TESL (Legends) REST API - Java")
                description.set("A REST interface to TESL Java SDK and Deck/Collection Decoding")
                url.set("https://github.com/markjfisher/tesl-java-sdk")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("mark.j.fisher")
                        name.set("Mark Fisher")
                        email.set("mark.j.fisher@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/markjfisher/tesl-java-sdk.git")
                    developerConnection.set("scm:git:ssh://github.com/markjfisher/tesl-java-sdk.git")
                    url.set("https://github.com/markjfisher/tesl-java-sdk")
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/markjfisher/tesl-java-sdk/issues")
                }
            }
        }
    }
    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            var releasesRepoUrl = uri("$buildDir/repos/releases")
            var snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
            if (project.hasProperty("live")) {
                releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials.username = sonatypeUsername
                credentials.password = sonatypePassword
            }
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

application {
    mainClassName = "tesl.Application"
}

allOpen {
    annotation("io.micronaut.aop.Around")
}

jib {
    from {
        image = "registry://adoptopenjdk/openjdk11"
    }
    container {
        jvmFlags = listOf("-Xms256m", "-Xmx256m")
        mainClass = "tesl.Application"
        args = listOf("")
        ports = listOf("80")
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
    to {
        // Docker Hub - credentials needed. See CREDENTIALS_DOCKER.md
        image = restDockerImageURL
        credHelper = "secretservice"
        tags = setOf("latest")
        // Can add a specific version to deploy to as follows:
        // tags = setOf("latest", "${project.version}")
    }
}