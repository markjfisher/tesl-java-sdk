package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeckTests {
    private val card1 = Card(name = "card1", id = "c1", code = "1c")
    private val card2 = Card(name = "card2", id = "c2", code = "2c")
    private val card3 = Card(name = "card3", id = "c3", code = "3c")
    private val card4 = Card(name = "card4", id = "c4", code = "4c")
    private val deck = Deck(cards = listOf(card4, card1, card3, card4, card2, card3, card1, card3, card4))

    @Test
    fun `can retrieve cards from a deck by count`() {
        assertThat(deck.of(0)).isEmpty()
        assertThat(deck.of(1)).containsExactly(card2)
        assertThat(deck.of(2)).containsExactly(card1)
        assertThat(deck.of(3)).containsExactly(card3, card4) // the returned list is sorted by name
        assertThat(deck.of(4)).isEmpty()
    }

    @Test
    fun `can create a code for a deck of cards`() {
        assertThat(deck.exportCode()).isEqualTo("SPAB2cAB1cAC3c4c")
    }

    @Test
    fun `real map data returns expected values`() {
        val card1 = CardCache.findByCode("aa")!!
        val card2 = CardCache.findByCode("dc")!!
        assertThat(Deck(listOf(card1)).exportCode()).isEqualTo("SPABaaAAAA")
        assertThat(Deck(listOf(card1, card2, card2)).exportCode()).isEqualTo("SPABaaABdcAA")
    }

    @Test
    fun `finding cards in deck by id`() {
        assertThat(deck.byId("c1")).isEqualTo(CardCount(card = card1, count = 2))
        assertThat(deck.byId("c2")).isEqualTo(CardCount(card = card2, count = 1))
        assertThat(deck.byId("c3")).isEqualTo(CardCount(card = card3, count = 3))
        assertThat(deck.byId("c4")).isEqualTo(CardCount(card = card4, count = 3))
    }

    @Test
    fun `importing code returns a deck with correct cards`() {
        // When
        val deck2 = Deck.importCode("SPABhhABcAACcLdh")

        // Then
        assertThat(deck2.byId("da35b318-c651-55e7-8cbe-c9b970d0cc7f").count).isEqualTo(1)
        assertThat(deck2.byId("edeabfdf-2875-5ddb-b03e-c800afe0934b").count).isEqualTo(2)
        assertThat(deck2.byId("1ad32fe0-2704-5ded-9f05-1fb73d7e5c55").count).isEqualTo(3)
        assertThat(deck2.byId("b89b7303-4404-53f7-9e79-bcc5b8eeab20").count).isEqualTo(3)
        assertThat(deck2.byId("xx")).isEqualTo(CardCount())
    }

    @Test
    fun `importing invalid code returns empty Deck`() {
        assertThat(Deck.importCode("SPABABAB").cards.size).isEqualTo(0)
    }

    @Test
    fun `exporting a code gives it as its canonical value`() {
        // Expect same card in multiple positions gets grouped and sorted
        assertThat(Deck.importCode("SPAEwZhihhhgAEhhhihgwZAA").exportCode()).isEqualTo("SPAAAAAEhghhhiwZ")

        // Expect unsorted lists to be sorted, note capital before lower, but it's consistent so doesn't matter
        assertThat(Deck.importCode("SPAGwZhivFhhhgulAFuBuyuzuAuxAEuGvAuFuH").exportCode()).isEqualTo("SPAGhghhhiulvFwZAFuAuBuxuyuzAEuFuGuHvA")
    }

    @Test
    fun `canonical codes`() {
        // Expect same card in multiple positions gets grouped and sorted
        assertThat(Deck.canonicalCode("SPAEwZhihhhgAEhhhihgwZAA")).isEqualTo("SPAAAAAEhghhhiwZ")

        // Expect unsorted lists to be sorted, note capital before lower, but it's consistent so doesn't matter
        assertThat(Deck.canonicalCode("SPAGwZhivFhhhgulAFuBuyuzuAuxAEuGvAuFuH")).isEqualTo("SPAGhghhhiulvFwZAFuAuBuxuyuzAEuFuGuHvA")
    }
}