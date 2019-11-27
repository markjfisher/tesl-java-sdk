package tesl.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.classgraph.ClassGraph
import io.github.classgraph.Resource

object CardCache {
    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

    private val cache = mutableMapOf<String, Card>()
    private lateinit var cards: List<Card>

    init {
        load()
    }

    @JvmStatic
    @Synchronized
    private fun load() {
        cache.clear()
        val result = ClassGraph().whitelistPathsNonRecursive("cards").scan()
        result.getResourcesWithExtension("json").forEachByteArray { _: Resource, content: ByteArray ->
            val card = mapper.readValue<Card>(content)
            cache[card.id] = card
        }
        cards = cache.values.toList()
    }

    @JvmStatic
    fun findByCode(code: String): Card? {
        if (code == "__") return null
        return cards.find { it.code == code }
    }

    @JvmStatic
    fun all(): List<Card> = cards

    @JvmStatic
    fun findById(id: String) = cache[id]

}