package tesl

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("tesl-rest")
class Configuration {
    var botCommandPostFix: String = ""
}