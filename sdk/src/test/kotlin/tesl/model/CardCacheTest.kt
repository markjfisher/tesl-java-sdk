package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardCacheTest {
    @Test
    fun `can find cards by code`() {
        val card = CardCache.findByCode("xd")
        assertThat(card?.name).isEqualTo("Prankster Mage")
    }

    @Test
    fun `return all cards`() {
        assertThat(CardCache.all()).hasSize(1302)
    }

    @Test
    fun `can find cards by id`() {
        val card = CardCache.findById("9f904315-d51c-5bc2-9d24-f24e95001d68")
        assertThat(card?.name).isEqualTo("Salvage")
    }
}