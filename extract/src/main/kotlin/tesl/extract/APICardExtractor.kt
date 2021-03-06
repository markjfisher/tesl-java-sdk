package tesl.extract

import tesl.oldmodel.Card
import tesl.oldmodel.APICardCache
import tesl.oldmodel.Decoder
import tesl.oldmodel.TESLCard
import java.io.File

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
            set = TESLCard.CardSet(name = "Monthly Rewards", id = "mr"),
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
        pranksterMage.write(File("$rootPath/prankstermage-xd.json"))
    }

    private fun copyExistingCards() {
        val cards = Card.all()
        cards.forEach { card ->
            card.code = Decoder.idToCodeMap[card.id] ?: "__"
            val teslCard = TESLCard.copy(card)
            val name = TESLCard.sanitize(card.name)
            teslCard.write(File("$rootPath/$name-${card.code}"))
        }
    }

}