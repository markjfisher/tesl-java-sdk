plugins {
    id("com.github.ben-manes.versions")
    kotlin("jvm") version "1.3.50"
}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.6.2"
        distributionType = Wrapper.DistributionType.ALL
    }
}

defaultTasks(
    ":sdk:clean", ":sdk:build"
)

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://kotlin.bintray.com/kotlinx")
    }

    group = "net.markjfisher"
    version = "1.0.0"

}
