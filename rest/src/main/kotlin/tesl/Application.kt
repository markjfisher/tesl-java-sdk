package tesl

import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import mu.KLogging
import tesl.model.CardCache

class Application {

    companion object : KLogging() {
        @JvmStatic
        fun main(args: Array<String>) {
            Micronaut.run(Application::class.java)
        }
    }

    @EventListener
    fun onStartup(e: ServerStartupEvent) {
        logger.info { "starting TESL REST service for url: ${e.source.url}" }
        CardCache.all()
    }

}
