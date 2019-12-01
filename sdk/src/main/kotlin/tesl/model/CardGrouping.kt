package tesl.model

open class CardGrouping(val cards: List<Card>) {
    // A map of count to list of cards with that count in the deck, e.g. {1: [Adoring Fan, Aela the Huntress], 2: [Aldmeri Patriot]}
    private val countToCardListMap = createCountToListOfCardsMap()

    // map of card.id to CardCount(Card, Int)
    private val deckCardMap: Map<String, CardCount> = createMapOfCardIdToCardCount()

    fun createCountToListOfCardsMap(): Map<Int, List<Card>> = cards
            .groupBy { it.id }                                     // Map<cardId, List<Card>>
            .map { it.value.size to it.value.first() }             // List<Pair<count, Card>>
            .groupBy { it.first }                                  // Map<count, List<Pair<count, Card>>>
            .map { it.key to it.value.map { p -> p.second }.sortedBy { card -> card.name } }      // List<Pair<count, List<Card (sorted by name)>>
            .toMap()                                               // Map<count, List<Card>>

    fun createMapOfCardIdToCardCount(): Map<String, CardCount> = cards
            .groupBy { it.id }
            .map { it.value.first().id to CardCount(card = it.value.first(), count = it.value.size) }
            .toMap()

    fun of(count: Int) = countToCardListMap.getOrDefault(count, emptyList())

    fun byId(cardId: String): CardCount {
        return deckCardMap.getOrDefault(cardId, CardCount())
    }
}

data class CardCount(
    val count: Int = 0,
    val card: Card? = null
)