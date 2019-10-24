package tesl.rest.reader

import tesl.model.Card
import tesl.model.CardCount
import tesl.model.Deck
import tesl.rest.ClassAbility
import tesl.rest.DeckClass

data class DeckAnalysis(
    private val deck: Deck
) {
    val commonCount: Int
    val rareCount: Int
    val epicCount: Int
    val legendaryCount: Int
    val soulgemCost: Int
    val prophecyCount: Int
    val creatureCount: Int
    val itemCount: Int
    val actionCount: Int
    val supportCount: Int

    val deckClass: DeckClass
    val className: String

    private val creaturesMap: Map<String, CardCount>
    private val itemsMap: Map<String, CardCount>
    private val actionsMap: Map<String, CardCount>

    private val supportsMap: Map<String, CardCount>

    init {
        commonCount = byRarity("Common").size
        rareCount = byRarity("Rare").size
        epicCount = byRarity("Epic").size
        legendaryCount = byRarity("Legendary").size
        soulgemCost = calculateSoulgemCost()
        prophecyCount = calculateProphecyCount()

        creaturesMap = byType("Creature")
        creatureCount = creaturesMap.map{ it.value.count }.sum()
        itemsMap = byType("Item")
        itemCount = itemsMap.map{ it.value.count }.sum()
        actionsMap = byType("Action")
        actionCount = actionsMap.map{ it.value.count }.sum()
        supportsMap = byType("Support")
        supportCount = supportsMap.map{ it.value.count }.sum()

        deckClass = calculateDeckClass()
        className = calculateClassName()
    }

    private fun calculateDeckClass(): DeckClass {
        val allClassAbilities = deck.cards
            .flatMap { it.attributes }.toSet()
            .map { ClassAbility.valueOf(it.toUpperCase()) }.toSet()

        val withoutNeutral = allClassAbilities - ClassAbility.NEUTRAL
        return DeckClass
            .values()
            .find { it.classAbilities.containsAll(withoutNeutral) } ?: DeckClass.NEUTRAL
    }

    private fun calculateClassName() = deckClass.name
            .replace("_", " ")
            .toLowerCase()
            .split(" ")
            .joinToString(" ") { it.capitalize() }

    private fun calculateProphecyCount(): Int {
        return deck.cards
            .flatMap { it.keywords }
            .groupBy { it }
            .map { (k, v) -> k to v.size }
            .toMap()["Prophecy"] ?: 0

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

    private fun byType(type: String): Map<String, CardCount> {
        return deck.cards
            .filter { it.type == type }
            .sortedBy { it.name }
            .groupBy { it.name }
            .map { (name, typeCards) ->
                val first = typeCards.first()
                name to CardCount(
                    count = typeCards.size,
                    card = first
                )
            }
            .toMap()
    }
}
