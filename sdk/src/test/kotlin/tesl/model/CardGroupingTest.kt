package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CardGroupingTest {
    private val card1 = Card(name = "card1", id = "c1", code = "1c")
    private val card2 = Card(name = "card2", id = "c2", code = "2c")
    private val card3 = Card(name = "card3", id = "c3", code = "3c")
    private val card4 = Card(name = "card4", id = "c4", code = "4c")
    private val cards = listOf(card4, card1, card3, card4, card2, card3, card1, card3, card4)
    private val deck = Deck(cards)

    @Test
    fun `count to list of cards`() {
        val group = CardGrouping(cards = cards)
        val expectedMap = mapOf(
            1 to listOf(card2),
            2 to listOf(card1),
            3 to listOf(card3, card4)
        )
        assertThat(group.createCountToListOfCardsMap()).isEqualTo(expectedMap)
    }

    @Test
    fun `card id to card counts`() {
        val group = CardGrouping(cards = cards)
        val expectedMap = mapOf(
            "c1" to CardCount(2, card1),
            "c2" to CardCount(1, card2),
            "c3" to CardCount(3, card3),
            "c4" to CardCount(3, card4)
        )
        val x = group.createMapOfCardIdToCardCount()
        assertThat(x).isEqualTo(expectedMap)
    }
}