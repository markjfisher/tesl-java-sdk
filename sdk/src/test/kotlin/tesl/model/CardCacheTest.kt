package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardCacheTest {
    @Test
    fun `can load cards into cache`() {
        val card = CardCache.findByCode("xd")
        assertThat(card?.name).isEqualTo("Prankster Mage")
    }
}