package tesl.rest.reader

import tesl.rest.model.Card
import tesl.rest.model.Deck

data class DeckAnalysis(
    private val deck: Deck
) {
    val commonCount: Int
    val rareCount: Int
    val epicCount: Int
    val legendaryCount: Int
    val soulgemCost: Int

    init {
        commonCount = byRarity("Common").size
        rareCount = byRarity("Rare").size
        epicCount = byRarity("Epic").size
        legendaryCount = byRarity("Legendary").size
        soulgemCost = calculateSoulgemCost()
    }

    private fun calculateSoulgemCost(): Int {
        return deck.cards.fold(0) { acc, card ->
            val cost = when (card.rarity) {
                "Legendary" -> 1200
                "Epic" -> 400
                "Rare" -> 100
                "Common" -> 50
                else -> 0
            }
            acc + cost
        }
    }

    private fun byRarity(rarity: String): List<Card> = deck.cards.filter { it.rarity == rarity }
}
