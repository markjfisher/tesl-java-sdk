package tesl.rest.controller

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.createDeckInfo
import tesl.rest.model.DeckInfo
import tesl.rest.reader.DeckReader

class TESLControllerTest {
    @Test
    fun `should return a deck info from deck reader`() {
        val reader = mockk<DeckReader>()
        val deckInfo: DeckInfo = createDeckInfo(code = "abc")
        every { reader.parse("abc") } returns deckInfo

        // When
        val d = TESLController(reader).info("abc").blockingGet()

        // Then
        assertThat(d?.code).isEqualTo("abc")
    }

    @Test
    fun `should return empty when code not parsed`() {
        val reader = mockk<DeckReader>()
        every { reader.parse("abc") } returns null

        // When
        val d = TESLController(reader).info("abc").blockingGet()

        // Then
        assertThat(d).isNull()
    }
}