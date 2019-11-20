rootProject.name = "tesl-java-sdk"

include(
    "extract",
    "sdk",
    "rest",
    "imageutils"
)

val kotlinVersion: String by settings
val springDependencyManagementVersion: String by settings
val gradleVersionsVersion: String by settings
val shadowVersion: String by settings
val jibVersion: String by settings
val javafxPluginVersion: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "com.github.ben-manes.versions" -> useVersion(gradleVersionsVersion)
                "org.jetbrains.kotlin.kapt" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.allopen" -> useVersion(kotlinVersion)
                "io.spring.dependency-management" -> useVersion(springDependencyManagementVersion)
                "com.github.johnrengelman.shadow" -> useVersion(shadowVersion)
                "com.google.cloud.tools.jib" -> useVersion(jibVersion)
                "org.jetbrains.kotlin.multiplatform" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.serialization" -> useVersion(kotlinVersion)
                "org.openjfx.javafxplugin" -> useVersion(javafxPluginVersion)
            }
        }
    }
}
