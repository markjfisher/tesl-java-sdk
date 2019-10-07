package tesl.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import tesl.resources.resourceFiles

class CardCache {
    companion object {
        private val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

        private val cache = mutableMapOf<String, Card>()

        @JvmStatic
        fun load() {
            cache.clear()
            resourceFiles("/cards").forEach { json ->
                val card = mapper.readValue<Card>(json)
                cache[card.id] = card
            }
        }

        @JvmStatic
        fun findByCode(code: String): Card? {
            if (cache.isEmpty()) load()
            if (code == "__") return null
            return cache.values.find { it.code == code }
        }

        @JvmStatic
        fun all(): List<Card> {
            if (cache.isEmpty()) load()
            return cache.values.toList()
        }

    }
}