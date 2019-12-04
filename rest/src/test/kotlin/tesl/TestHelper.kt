package tesl

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
