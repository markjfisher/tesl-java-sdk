import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
    signing
}

group = "net.markjfisher"
version = "1.0.16"
val teslArchiveBaseName = "tesl-java-sdk"

val sonatypeUsername: String by project
val sonatypePassword: String by project

val uuidGeneratorVersion: String by project
val jacksonVersion: String by project
val konfigVersion: String by project
val classgraphVersion: String by project

val kotlinLoggingVersion: String by project
val logbackClassicVersion: String by project
val logbackEncoderVersion: String by project

val assertJVersion: String by project
val mockkVersion: String by project
val junitJupiterEngineVersion: String by project

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")
    implementation("com.fasterxml.uuid:java-uuid-generator:$uuidGeneratorVersion")
    implementation("com.natpryce:konfig:$konfigVersion")
    implementation("io.github.classgraph:classgraph:$classgraphVersion")

    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

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
                name.set("TESL (Legends) Standalone SDK - Java")
                description.set("A stand-alone library for working with TESL Cards, Decks and Collections")
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
