package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CollectionTests {
    private val card1 = Card(name = "card1", id = "c1", code = "1c")
    private val card2 = Card(name = "card2", id = "c2", code = "2c")
    private val card3 = Card(name = "card3", id = "c3", code = "3c")
    private val card4 = Card(name = "card4", id = "c4", code = "4c")
    private val collection = Collection(cards = listOf(card4, card1, card3, card4, card2, card3, card1, card3, card4))

    @Test
    fun `can retrieve cards from a collection by count`() {
        assertThat(collection.of(0)).isEmpty()
        assertThat(collection.of(1)).containsExactly(card2)
        assertThat(collection.of(2)).containsExactly(card1)
        assertThat(collection.of(3)).containsExactly(card3, card4) // the returned list is sorted by name
        assertThat(collection.of(4)).isEmpty()
    }

    @Test
    fun `can create a code for a collection of cards`() {
        assertThat(collection.exportCode()).isEqualTo("SP!\"2c!\"1c!#3c4c")
    }

    @Test
    fun `real map data returns expected values`() {
        val card1 = CardCache.findByCode("aa")!!
        val card2 = CardCache.findByCode("dc")!!
        assertThat(Collection(listOf(card1)).exportCode()).isEqualTo("SP!\"aa!!!!")
        assertThat(Collection(listOf(card1, card2, card2)).exportCode()).isEqualTo("SP!\"aa!\"dc!!")
    }

    @Test
    fun `finding cards in collection by id`() {
        assertThat(collection.byId("c1")).isEqualTo(CardCount(card = card1, count = 2))
        assertThat(collection.byId("c2")).isEqualTo(CardCount(card = card2, count = 1))
        assertThat(collection.byId("c3")).isEqualTo(CardCount(card = card3, count = 3))
        assertThat(collection.byId("c4")).isEqualTo(CardCount(card = card4, count = 3))
    }

    @Test
    fun `importing invalid code returns empty Collection`() {
        assertThat(Collection.importCode("SP!#!#!#").cards.size).isEqualTo(0)
    }

    @Test
    fun `exporting a code gives it as its canonical value`() {
        // Expect same card in multiple positions gets grouped and sorted
        assertThat(Collection.importCode("SP!%wZhihhhg!%hhhihgwZ!!").exportCode()).isEqualTo("SP!!!!!%%hghhhiwZ")

        // Expect unsorted lists to be sorted, note capital before lower, but it's consistent so doesn't matter
        assertThat(Collection.importCode("SP!'wZhivFhhhgul!&uBuyuzuAux!%uGvAuFuH").exportCode()).isEqualTo("SP!'hghhhiulvFwZ!&uAuBuxuyuz!%%uFuGuHvA")
    }

    @Test
    fun `canonical codes`() {
        // Expect same card in multiple positions gets grouped and sorted
        assertThat(Collection.canonicalCode("SP!%wZhihhhg!%hhhihgwZ!!")).isEqualTo("SP!!!!!%%hghhhiwZ")

        // Expect unsorted lists to be sorted, note capital before lower, but it's consistent so doesn't matter
        assertThat(Collection.canonicalCode("SP!'wZhivFhhhgul!&uBuyuzuAux!%uGvAuFuH")).isEqualTo("SP!'hghhhiulvFwZ!&uAuBuxuyuz!%%uFuGuHvA")
    }

}