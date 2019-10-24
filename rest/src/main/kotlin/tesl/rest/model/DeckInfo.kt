package tesl.rest.model

data class DeckInfo(
    val code: String,
    val className: String,
    val commonCount: Int,
    val rareCount: Int,
    val epicCount: Int,
    val legendaryCount: Int,
    val soulgemCost: Int,
    val creatureCount: Int,
    val actionCount: Int,
    val itemCount: Int,
    val supportCount: Int,
    val cards: List<CardInfoCount>
)

data class CardInfoCount(
    val count: Int,
    val card: CardInfo
)

// Cut down version of Card
data class CardInfo(
    val id: String,
    val name: String,
    val rarity: String,
    val mana: Int,
    val power: Int,
    val health: Int,
    val type: String,
    val attributes: List<String>,
    val keywords: List<String>
)