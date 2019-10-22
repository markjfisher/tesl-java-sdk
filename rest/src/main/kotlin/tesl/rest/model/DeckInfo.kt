package tesl.rest.model

data class DeckInfo(
    val code: String,
    val commonCount: Int,
    val rareCount: Int,
    val epicCount: Int,
    val legendaryCount: Int,
    val soulgemCost: Int,
    val cards: List<CardInfo>
)



data class CardInfo(
    val id: String,
    val name: String,
    val mana: Int,
    val power: Int,
    val health: Int,
    val type: String,
    val attributes: List<String>,
    val keywords: List<String>
)