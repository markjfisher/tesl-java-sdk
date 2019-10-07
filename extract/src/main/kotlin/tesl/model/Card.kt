package tesl.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Card(
    val name: String,
    val rarity: String = "",
    val type: String = "",
    val subtypes: List<String> = emptyList(),
    val cost: Int = -1,
    val power: Int = -1,
    val health: Int = -1,
    val set: CardSet = CardSet(),
    val collectible: Boolean = false,
    val soulSummon: String = "",
    val soulTrap: String = "",
    val text: String = "",
    val attributes: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val unique: Boolean = true,
    val imageUrl: String = "",
    val id: String = "",
    var code: String = ""
) {
    companion object {
        private val queryBuilder = QueryBuilder()
        private const val RESOURCE_NAME = "cards"

        @JvmStatic
        fun all(): List<Card> {
            return where(emptyMap())
        }

        @JvmStatic
        fun find(id: String): Card? {
            return queryBuilder.find(resource = RESOURCE_NAME, id = id, cls = CardSingle::class.java)?.card
        }

        @JvmStatic
        fun where(predicates: Map<String, String>): List<Card> {
            return queryBuilder.where(resource = RESOURCE_NAME, cls = Cards::class.java, predicates = predicates) { cards, cardList ->
                cardList.addAll(cards?.cards ?: emptyList())
            }
        }
    }
}

data class Cards(
    val cards: List<Card>,

    @JsonProperty("_pageSize")
    override val pageSize: Int,
    @JsonProperty("_totalCount")
    override val totalCount: Int
) : ResultCounters(pageSize, totalCount)

data class CardSingle(
    val card: Card
)

data class CardSet(
    val name: String = "",
    val id: String = "",
    @JsonProperty(value = "_self")
    val self: String = ""
)