rootProject.name = "tesl-java-sdk"

include(
    "extract",
    "sdk"
)

val kotlinVersion: String by settings
val kotlinXSerialisationVersion: String by settings
val gradleVersionsVersion: String by settings
val shadowVersion: String by settings

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when(requested.id.id) {
                "org.jetbrains.kotlin.jvm" -> useVersion(kotlinVersion)
                "com.github.ben-manes.versions" -> useVersion(gradleVersionsVersion)
                "org.jetbrains.kotlin.kapt" -> useVersion(kotlinVersion)
                "org.jetbrains.kotlin.plugin.allopen" -> useVersion(kotlinVersion)
                "com.github.johnrengelman.shadow" -> useVersion(shadowVersion)
//                "kotlinx-serialization" -> {
//                    useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
//                }
            }
        }
    }
}
