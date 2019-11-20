package aligner.controller

import javafx.embed.swing.JFXPanel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import tesl.model.CardCache

class CardAlignControllerTest {
    private val fxPanel = JFXPanel() // hack to initialize javafx, and stop the 'toolkit not initialized' error

    @Test
    fun `should convert url to relative project path`() {
        val card = CardCache.findById("9f904315-d51c-5bc2-9d24-f24e95001d68") ?: fail("Didn't find card")
        val controller = CardAlignController()
        val cardUrl = controller.cardUrl(card)
        assertThat(controller.toRelativePath(cardUrl)).isEqualTo("rest/src/main/resources/images/cards/salvage.png")
    }

    // Oddly, running tests, File(".") is the imageutils dir, when running the app it's the project dir.
    @Test
    fun `should convert relative path to a url`() {
        val cardPath = "rest/src/main/resources/images/cards/salvage.png"
        val controller = CardAlignController()
        val url = controller.toUrl(cardPath)
        assertThat(url).startsWith("file:")
        assertThat(url).endsWith("rest/src/main/resources/images/cards/salvage.png")
    }
}