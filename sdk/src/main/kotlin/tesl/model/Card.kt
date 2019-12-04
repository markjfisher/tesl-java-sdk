package tesl.model

data class Card(
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
    val imageUrl: String = "",
    val id: String,
    val code: String
) {
    companion object {
        fun createFrom(
            card: Card,
            name: String = card.name,
            rarity: String = card.rarity,
            type: String = card.type,
            subtypes: List<String> = card.subtypes,
            cost: Int = card.cost,
            power: Int = card.power,
            health: Int = card.health,
            set: Map<String, String> = card.set,
            collectible: Boolean = card.collectible,
            soulSummon: Int = card.soulSummon,
            soulTrap: Int = card.soulTrap,
            text: String = card.text,
            attributes: List<String> = card.attributes,
            keywords: List<String> = card.keywords,
            unique: Boolean = card.unique,
            imageUrl: String = card.imageUrl,
            id: String = card.id,
            code: String = card.code
        ): Card {
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
                imageUrl = imageUrl,
                id = id,
                code = code
            )
        }

    }

}