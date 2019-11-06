package tesl.bot

import com.jessecorbett.diskord.util.toFileData
import mu.KotlinLogging
import tesl.model.Card
import tesl.model.Deck
import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.reader.DeckAnalysis
import tesl.rest.reader.ImageCreator
import java.lang.Integer.min

private val logger = KotlinLogging.logger {}

object DeckCommands {
    private val allCommands = mapOf(
        "help" to HelpDeckCommand,
        "info" to InfoDeckCommand,
        "detail" to DetailDeckCommand,
        "image" to ImageDeckCommand,
        "validate" to ValidateDeckCommand
    )

    fun find(name: String): DeckCommand = allCommands[name] ?: HelpDeckCommand
    fun allHelp() = allCommands.values.joinToString("\n") { it.help() }
}


interface DeckCommand {
    fun run(args: List<String>, mention: String, username: String): ReplyData
    fun help(): String
}

object HelpDeckCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        val helpText = DeckCommands.allHelp()
        return ReplyData(text = listOf("```$helpText```"))
    }

    override fun help(): String {
        return "help - shows this help"
    }
}

object InfoDeckCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        return ReplyData(text = show(args, mention, username, "info"))
    }

    override fun help(): String {
        return "info - displays summary information about a deck"
    }
}

object DetailDeckCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        return ReplyData(text = show(args, mention, username, "detail"))
    }

    override fun help(): String {
        return "detail - displays detailed information about a deck, as info but with addition creatures/items/support/actions breakdown"
    }
}

object ImageDeckCommand : BaseDeckCommand() {
    override fun help(): String {
        return "image - creates a graphical image of the deck."
    }

    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        if (args.size != 1) {
            return ReplyData(text = listOf("$mention: Please supply a single deck code."))
        }

        val deckCode = args[0]
        logger.info { "User: $username asked for image for code: $deckCode" }

        val fileName = "${username.substring(0, min(username.length, 10))}-${deckCode.substring(2, min(deckCode.length - 2, 12))}.png"
            .filter { it.isLetterOrDigit() || it == '.' || it == '-' }

        val fileData = imageCreator.createDeckImage(deckCode)?.toFileData(fileName)

        return if (fileData == null) {
            ReplyData(
                text = listOf("$mention - unable to create deck from given code: $deckCode")
            )
        } else {
            ReplyData(
                text = listOf("$mention - here is your deck for $deckCode"),
                fileData = fileData
            )
        }
    }

}

object ValidateDeckCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        if (args.size != 1) {
            return ReplyData(text = listOf("$mention: Please supply a single deck code."))
        }

        val deckCode = args[0]
        val checkResults = Decoder(DecoderType.DECK).checkImportCode(deckCode)
        if (!checkResults.first) return ReplyData(text = listOf("$mention: Non valid import code."))

        val returnText = if (checkResults.second.isEmpty()) "$mention: Deck code is valid and has no unknown card codes." else "$mention: Following codes are unknown: ${checkResults.second.joinToString(", ")}"
        return ReplyData(text = listOf(returnText))
    }

    override fun help(): String {
        return "validate - validates a deck code is in correct format, and all card codes are known."
    }
}

abstract class BaseDeckCommand() : DeckCommand {
    lateinit var imageCreator: ImageCreator

    fun show(args: List<String>, mention: String, username: String, type: String): List<String> {
        if (args.size != 1) {
            return listOf("$mention: Please supply a single deck code.")
        }

        val deckCode = args[0]
        logger.info { "User: $username asked for $type for code: $deckCode" }
        val deck = Deck.importCode(deckCode)
        val a = DeckAnalysis(deck)

        val reply = when (type) {
            "info", "detail" -> {
                val line1 = String.format("%-10s: %-5d   %-10s: %-5d", "Common", a.commonCount, "Actions", a.actionCount)
                val line2 = String.format("%-10s: %-5d   %-10s: %-5d", "Rare", a.rareCount, "Items", a.itemCount)
                val line3 = String.format("%-10s: %-5d   %-10s: %-5d", "Epic", a.epicCount, "Support", a.supportCount)
                val line4 = String.format("%-10s: %-5d   %-10s: %-5d", "Legendary", a.legendaryCount, "Creatures", a.creatureCount)
                val line5 = String.format("%-10s: %-10d", "Soulgems", a.soulgemCost)

                """|$mention : $deckCode
                |```$line1
                |$line2
                |$line3
                |$line4
                |$line5
                |
                |Class    : ${a.className} [${a.attributesText}]
                |Unique   : ${a.totalUnique}
                |Total    : ${a.totalCards} (1s: ${deck.of(1).size}, 2s: ${deck.of(2).size}, 3s: ${deck.of(3).size})
                |
                |Mana Curve
                |${a.createManaString()}```
                |""".trimMargin(marginPrefix = "|")
            }

            else -> "Unknown type: $type"
        }

        return if (type == "detail") {
            val creatureData = collectCardData(deck, "Creature")
            val actionData = collectCardData(deck, "Action")
            val itemData = collectCardData(deck, "Item")
            val supportData = collectCardData(deck, "Support")
            val detailCreatures = splitToList("Creature", mention, creatureData)
            val detailActions = splitToList("Action", mention, actionData)
            val detailItems = splitToList("Item", mention, itemData)
            val detailSupports = splitToList("Support", mention, supportData)

            listOf(reply) + detailCreatures + detailActions + detailItems + detailSupports
        } else {
            listOf(reply)
        }
    }

    private fun splitToList(typeName: String, mention: String, data: String): List<String> {
        return when {
            data.length > 1900 -> {
                val lines = data.lines()
                val parts = mutableListOf<String>()
                var initialString = "$mention\n${typeName}s:```"
                var currentString = ""
                lines.forEach { line ->
                    currentString += "$initialString$line\n"
                    initialString = ""
                    if (currentString.length > 1500) {
                        parts.add("$currentString```")
                        currentString = "$mention```"
                    }
                }
                if (currentString != "$mention```") parts.add("$currentString```")
                parts.toList()
            }
            data.isNotBlank() -> listOf("$mention\n${typeName}s:```$data```")
            else -> emptyList()
        }
    }

    private fun collectCardData(deck: Deck, type: String): String {
        val maxCardNameLength = deck.cards.fold(0) { max, c ->
            if (c.name.length > max) c.name.length else max
        }

        val maxTypesLength = deck.cards.fold(0) { max, c ->
            // Use "[All]" for Reflective Automaton
            val len = if (c.name == "Reflective Automaton") 5 else c.subtypes.joinToString(", ").length
            if (len > max) len else max
        }

        val maxSetIdLength = deck.cards.fold(0) { max, c ->
            val len = c.set["id"]?.length ?: 0
            if (len > max) len else max
        }

        val maxHealthLength = if(deck.cards.any { it.health >= 10 }) 2 else 1
        val maxPowerLength = if(deck.cards.any { it.power >= 10 }) 2 else 1
        val maxCostLength = if(deck.cards.any { it.cost >= 10 }) 2 else 1

        val of1Data = byType(deck.of(1), 1, type, maxCardNameLength, maxTypesLength, maxSetIdLength, maxCostLength, maxPowerLength, maxHealthLength)
        val of2Data = byType(deck.of(2), 2, type, maxCardNameLength, maxTypesLength, maxSetIdLength, maxCostLength, maxPowerLength, maxHealthLength)
        val of3Data = byType(deck.of(3), 3, type, maxCardNameLength, maxTypesLength, maxSetIdLength, maxCostLength, maxPowerLength, maxHealthLength)
        return listOf(of1Data, of2Data, of3Data).mapNotNull { if (it.isBlank()) null else it }.joinToString("\n")
    }

    private fun byType(
        cards: List<Card>,
        size: Int,
        type: String,
        maxCardNameLength: Int,
        maxTypesLength: Int,
        maxSetIdLength: Int,
        maxCostLength: Int,
        maxPowerLength: Int,
        maxHealthLength: Int
    ): String {

        return cards
            .asSequence()
            .filter { it.type == type }
            .sortedBy { it.cost }
            .map { card ->
                val cost = formatNumber(card.cost, maxCostLength)
                val power = formatNumber(card.power, maxPowerLength)
                val health = formatNumber(card.health, maxHealthLength)
                val costPowerHealthString = "[$cost/$power/$health]"

                val namesString = String.format("%-${maxCardNameLength}s", card.name.take(maxCardNameLength))
                val rarityString = String.format("%-6s", card.rarity.take(6))
                val setName = String.format("| %-${maxSetIdLength}s", card.set["id"])
                val typesString = if (card.subtypes.isNotEmpty()) {
                    val partialTypesString = if (card.name == "Reflective Automaton") "[All]" else card.subtypes.joinToString(",")
                    String.format("| %-${maxTypesLength}s", partialTypesString)
                } else ""
                "$size x $namesString $costPowerHealthString $rarityString $setName $typesString"

            }
            .joinToString("\n")
    }

    private fun formatNumber(n: Int, max: Int): String {
        val asString = if (n >= 0) "$n" else "-"
        return if(max > asString.length) " $asString" else asString
    }
}
