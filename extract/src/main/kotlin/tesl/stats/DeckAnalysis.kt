package tesl.stats

import tesl.model.*
import tesl.model.Collection

// TODO make this part of SDK

data class DeckAnalysis(
    private val deck: CardGrouping
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
    val totalUnique: Int
    val totalCards: Int
    val deckClass: DeckClass
    val className: String
    val attributesCount: Map<String, Int>
    val attributesText: String
    val manaCurve: Map<Int, Int>
    val cardCountSorted: List<CardCount>

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
        creatureCount = creaturesMap.map { it.value.count }.sum()
        itemsMap = byType("Item")
        itemCount = itemsMap.map { it.value.count }.sum()
        actionsMap = byType("Action")
        actionCount = actionsMap.map { it.value.count }.sum()
        supportsMap = byType("Support")
        supportCount = supportsMap.map { it.value.count }.sum()

        deckClass = calculateDeckClass()
        className = calculateClassName()

        attributesCount = groupAndCount { it.attributes }
        attributesText = attributesCount.map { (name, count) -> "$name: $count" }.joinToString(", ")

        val c1 = deck.of(1).size
        val c2 = deck.of(2).size
        val c3 = deck.of(3).size

        totalUnique = c1 + c2 + c3
        totalCards = c1 + c2 * 2 + c3 * 3

        manaCurve = calculateManaCurve()

        cardCountSorted = createSortedCardCount()

    }

    private fun createSortedCardCount(): List<CardCount> {
        // Sorts all the cards by cost, then name, and then groups the same card into a count to give List<CardCount>
        // so that each card is represented only once in the list, but its count is still captured in the ordering
        val cardSorter = when (deck) {
            // is Collection -> compareBy<Card> { it.cost }.thenBy { it.attributes.joinToString("") }.thenBy { it.name }
            is Collection -> compareBy<Card> { it.attributes.joinToString("") }.thenBy { it.cost }.thenBy { it.name }
            else -> compareBy<Card> { it.cost }.thenBy { it.name }
        }

        return deck.cards
            .sortedWith(cardSorter)
            .groupBy { Pair(it.cost, it.name) }
            .map {
                CardCount(count = it.value.size, card = it.value.first())
            }
    }

    private fun calculateManaCurve(): Map<Int, Int> {
        val costToCountMap = deck.cards
            .groupBy { it.cost }
            .toSortedMap()
            .map { entry ->
                val cost = entry.key
                val count = entry.value
                    .groupBy { card -> card.name }
                    .map { it.value.size }
                    .sum()
                (cost to count)
            }
            .toMap()

        return (0..50).fold(mutableMapOf(), { acc, cost ->
            val x = if (cost < 8) cost else 7
            val y = costToCountMap[cost] ?: 0
            val sevenPlus = acc.getOrDefault(7, 0)
            acc[x] = sevenPlus + y
            acc
        })

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

    private fun groupAndCount(mapper: (card: Card) -> List<String>): Map<String, Int> {
        return deck.cards
            .flatMap { mapper(it) }
            .groupBy { it }
            .map { (k, v) -> k to v.size }
            .toMap()
    }

    fun createManaString(): String {
        val maxValue = manaCurve.values.max()!!
        val maxValueLength = "$maxValue".length
        val increment = maxValue / 20.0
        val maxLabelLength = 4

        return manaCurve.map { (cost, count) ->
            val barChunks = ((count * 8) / increment).toInt().div(8)
            val remainder = ((count * 8) / increment).toInt().rem(8)

            var bar = "█".repeat(barChunks)
            if (remainder > 0) {
                bar += ('█'.toInt() + (8 - remainder)).toChar()
            }
            if (bar == "") {
                bar = "▏"
            }

            val costText = if (cost < 7) "$cost" else "7+"
            " ${costText.padEnd(maxLabelLength)}| ${count.toString().padEnd(maxValueLength)} $bar"
        }.joinToString("\n")

    }

}
