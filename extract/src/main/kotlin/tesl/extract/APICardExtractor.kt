package tesl.extract

import tesl.model.Card
import tesl.model.APICardCache
import tesl.model.Decoder
import tesl.model.TESLCard

object APICardExtractor {
    private const val rootPath = "/tmp/cards"

    @JvmStatic
    fun main(args: Array<String>) {
        println("Loading cache")
        APICardCache.load()

        println("Copying cards")
        copyExistingCards()

        println("Creating new cards")
        createNewCards()
    }

    private fun createNewCards() {
        val pranksterMage = TESLCard(
            name = "Prankster Mage",
            rarity = "Epic",
            type = "Creature",
            subtypes = listOf("Khajiit"),
            cost = 5,
            power = 5,
            health = 3,
            set = mapOf("name" to "Monthly Rewards", "id" to "mr"),
            collectible = true,
            soulSummon = 400,
            soulTrap = 100,
            text = "Ward. While Prankster Mage has a Ward, he has Guard. When Prankster Mage's Ward is broken, he gains Cover.",
            attributes = listOf("Intelligence", "Agility"),
            keywords = listOf("Ward", "Guard"),
            unique = false,
            imageUrl = "https://www.legends-decks.com/img_cards/prankstermage.png",
            code = "xd"
        )
        pranksterMage.write(rootPath)
    }

    private fun copyExistingCards() {
        val cards = Card.all()
        cards.forEach { card ->
            card.code = Decoder.idToCodeMap[card.id] ?: "__"
            val teslCard = TESLCard.copy(card)
            teslCard.write(rootPath)
        }
    }

}