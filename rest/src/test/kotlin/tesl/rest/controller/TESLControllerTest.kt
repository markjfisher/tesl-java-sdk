package tesl.rest.controller

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.createDeckInfo
import tesl.rest.model.DeckInfo
import tesl.rest.reader.DeckInfoCreator
import tesl.rest.reader.ImageCreator

class TESLControllerTest {
    private val imageCreator = mockk<ImageCreator>()
    @Test
    fun `should return a deck info from deck reader`() {
        val reader = mockk<DeckInfoCreator>()
        val deckInfo: DeckInfo = createDeckInfo(code = "abc")
        every { reader.parse("abc") } returns deckInfo

        // When
        val d = TESLController(reader, imageCreator).info("abc").blockingGet()

        // Then
        assertThat(d?.code).isEqualTo("abc")
    }

    @Test
    fun `should return empty when code not parsed`() {
        val reader = mockk<DeckInfoCreator>()
        every { reader.parse("abc") } returns null

        // When
        val d = TESLController(reader, imageCreator).info("abc").blockingGet()

        // Then
        assertThat(d).isNull()
    }
}