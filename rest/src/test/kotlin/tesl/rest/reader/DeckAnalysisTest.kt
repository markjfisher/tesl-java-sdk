package tesl.rest.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.model.Deck

class DeckAnalysisTest {
    private val redDeck = Deck.importCode("SPAEcCiPhypuAEsGdzkawiAElDdDmgmV")
    private val redDeckAnalysis = DeckAnalysis(redDeck)

    @Test
    fun `rarity count test`() {
        assertThat(redDeckAnalysis.commonCount).isEqualTo(6)
        assertThat(redDeckAnalysis.rareCount).isEqualTo(6)
        assertThat(redDeckAnalysis.epicCount).isEqualTo(6)
        assertThat(redDeckAnalysis.legendaryCount).isEqualTo(6)
    }

    @Test
    fun `soulgem count`() {
        assertThat(redDeckAnalysis.soulgemCost).isEqualTo(10500)
    }
}