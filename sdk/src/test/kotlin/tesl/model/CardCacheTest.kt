package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardCacheTest {
    @Test
    fun `can find cards via cache`() {
        val card = CardCache.findByCode("xd")
        assertThat(card?.name).isEqualTo("Prankster Mage")
    }

    @Test
    fun `return all cards`() {
        assertThat(CardCache.all()).hasSize(1297)
    }
}