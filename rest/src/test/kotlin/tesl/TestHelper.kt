package tesl

import tesl.model.Card
import tesl.rest.model.DeckInfo

fun createDeckInfo(
    code: String
): DeckInfo {
    return DeckInfo(
        code = code,
        className = "",
        commonCount = 0,
        rareCount = 0,
        epicCount = 0,
        legendaryCount = 0,
        soulgemCost = 0,
        creatureCount = 0,
        actionCount = 0,
        itemCount = 0,
        supportCount = 0,
        cards = emptyList()
    )
}

// Make it easy to create Cards from minimal data for testing
data class TestCard(
    val name: String,
    val rarity: String = "",
    val type: String = "",
    val subtypes: List<String> = emptyList(),
    val cost: Int = -1,
    val power: Int = -1,
    val health: Int = -1,
    val set: Map<String, String> = emptyMap(),
    val collectible: Boolean = false,
    val soulSummon: Int = -1,
    val soulTrap: Int = -1,
    val text: String = "",
    val attributes: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val unique: Boolean = true,
    val isAlt: Boolean = false,
    val imageUrl: String = "",
    val id: String = "",
    val code: String = ""
) {
    fun createCard(): Card {
        return Card(
            name = name,
            rarity = rarity,
            type = type,
            subtypes = subtypes,
            cost = cost,
            power = power,
            health = health,
            set = set,
            collectible = collectible,
            soulSummon = soulSummon,
            soulTrap = soulTrap,
            text = text,
            attributes = attributes,
            keywords = keywords,
            unique = unique,
            isAlt = isAlt,
            imageUrl = imageUrl,
            id = id,
            code = code
        )
    }
}