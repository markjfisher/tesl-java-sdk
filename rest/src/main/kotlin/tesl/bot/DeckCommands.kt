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
        "help" to HelpCommand,
        "info" to InfoCommand,
        "detail" to DetailCommand,
        "image" to ImageCommand,
        "validate" to ValidateCommand
    )

    fun find(name: String): DeckCommand = allCommands[name] ?: HelpCommand
    fun allHelp() = allCommands.values.joinToString("\n") { it.help() }
}


interface DeckCommand {
    fun run(args: List<String>, mention: String, username: String): ReplyData
    fun help(): String
}

object HelpCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        val helpText = DeckCommands.allHelp()
        return ReplyData(text = listOf("```$helpText```"))
    }

    override fun help(): String {
        return "help - shows this help"
    }
}

object InfoCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        return ReplyData(text = show(args, mention, username, "info"))
    }

    override fun help(): String {
        return "info - displays summary information about a deck (class, keywords, types, etc. and mana curve.)"
    }
}

object DetailCommand : BaseDeckCommand() {
    override fun run(args: List<String>, mention: String, username: String): ReplyData {
        return ReplyData(text = show(args, mention, username, "detail"))
    }

    override fun help(): String {
        return "detail - displays detailed information about a deck, as info but with addition creatures/items/support/actions breakdown"
    }
}

object ImageCommand : BaseDeckCommand() {
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

object ValidateCommand : BaseDeckCommand() {
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
        val da = DeckAnalysis(deck)

        val reply = when (type) {
            "info", "detail" -> {
                val line1 = String.format("%-10s: %-5d   %-10s: %-5d", "Common", da.commonCount, "Actions", da.actionCount)
                val line2 = String.format("%-10s: %-5d   %-10s: %-5d", "Rare", da.rareCount, "Items", da.itemCount)
                val line3 = String.format("%-10s: %-5d   %-10s: %-5d", "Epic", da.epicCount, "Support", da.supportCount)
                val line4 = String.format("%-10s: %-5d   %-10s: %-5d", "Legendary", da.legendaryCount, "Creatures", da.creatureCount)
                val line5 = String.format("%-10s: %-10d", "Soulgems", da.soulgemCost)

                """|$mention : $deckCode
                |```$line1
                |$line2
                |$line3
                |$line4
                |$line5
                |
                |Class    : ${da.className}
                |Unique   : ${da.totalUnique}
                |Total    : ${da.totalCards}```
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
            val len = c.subtypes.joinToString(", ").length
            if (len > max) len else max
        }

        val maxSetIdLength = deck.cards.fold(0) { max, c ->
            val len = c.set["id"]?.length ?: 0
            if (len > max) len else max
        }

        val of1Data = byType(deck.of(1), 1, type, maxCardNameLength, maxTypesLength, maxSetIdLength)
        val of2Data = byType(deck.of(2), 2, type, maxCardNameLength, maxTypesLength, maxSetIdLength)
        val of3Data = byType(deck.of(3), 3, type, maxCardNameLength, maxTypesLength, maxSetIdLength)
        return listOf(of1Data, of2Data, of3Data).mapNotNull { if (it.isBlank()) null else it }.joinToString("\n")
    }

    private fun byType(
        cards: List<Card>,
        size: Int,
        type: String,
        maxCardNameLength: Int,
        maxTypesLength: Int,
        maxSetIdLength: Int
    ): String {

        return cards
            .asSequence()
            .filter { it.type == type }
            .sortedBy { it.cost }
            .map { card ->
                val cost = card.cost
                val power = if (card.power >= 0) "${card.power}" else "-"
                val health = if (card.health >= 0) "${card.health}" else "-"
                val costPowerHealthString = "[$cost/$power/$health]"

                val namesString = String.format("%-${maxCardNameLength}s", card.name.take(maxCardNameLength))
                val rarityString = String.format("%-6s", card.rarity.take(6))
                val setName = String.format("| %-${maxSetIdLength}s", card.set["id"])
                val typesString = if (card.subtypes.isNotEmpty()) String.format("| %-${maxTypesLength}s", card.subtypes.joinToString(",")) else ""
                "$size x $namesString $costPowerHealthString $rarityString $setName $typesString"

            }
            .joinToString("\n")
    }
}
