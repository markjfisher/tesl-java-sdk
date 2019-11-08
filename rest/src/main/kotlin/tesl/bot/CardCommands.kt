package tesl.bot

import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.api.rest.Embed
import com.jessecorbett.diskord.api.rest.EmbedAuthor
import com.jessecorbett.diskord.api.rest.EmbedImage
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.pngAvatar
import com.jessecorbett.diskord.util.toFileData
import me.xdrop.fuzzywuzzy.FuzzySearch
import mu.KotlinLogging
import tesl.model.CardCache
import tesl.rest.reader.toByteArray
import javax.imageio.ImageIO

private val logger = KotlinLogging.logger {}

object CardCommands {
    private val allCommands = mapOf(
        "help" to HelpCardCommand,
        "find" to FindCardCommand,
        "search" to SearchCardCommand
    )
    fun find(name: String): CardCommand = allCommands[name] ?: HelpCardCommand
    fun allHelp() = "Type '!card <command> <args>' where commands are:\n\n" + allCommands.values.filter { !it.isHidden() }.joinToString("\n") { it.help() }
}

interface CardCommand {
    fun run(args: List<String>, author: User): List<ReplyData>
    fun help(): String
    fun isHidden(): Boolean
}

object HelpCardCommand: BaseCardCommand() {
    override fun run(args: List<String>, author: User): List<ReplyData> {
        val helpText = CardCommands.allHelp()
        return listOf(ReplyData(text = listOf("```$helpText```")))
    }

    override fun help(): String {
        return "help - shows this help"
    }

    override fun isHidden() = true
}

object SearchCardCommand: BaseCardCommand() {
    private val allCards = CardCache.all()

    override fun isHidden() = false

    override fun run(args: List<String>, author: User): List<ReplyData> {
        if (args.isEmpty()) return listOf(ReplyData(text = listOf("${author.mention} please supply a search term.")))

        val searchTerm = args.joinToString(" ")
        val extractSorted = FuzzySearch.extractSorted(searchTerm, allCards) { it.name }
        logger.info { "User: ${author.username} searching for '$searchTerm'"}

        if (extractSorted.isEmpty() || extractSorted.first().score <= 71) return listOf(ReplyData(text = listOf("${author.mention}, sorry, no matches for $searchTerm.")))

        val numToTake = if (extractSorted.first().score == 100) 1 else 3
        return extractSorted
            .filter { it.score >= 72 }
            .take(numToTake)
            .map {
                val card = it.referent
                val imageFileName = card.imageUrl.substringAfterLast("/")
                val imageResource = this::class.java.classLoader.getResource("images/cards/${imageFileName}")

                val data = ReplyData(
                    text = listOf(""),
                    embed = Embed(
                        title = it.referent.name,
                        author = EmbedAuthor(name = author.username, authorImageUrl = author.pngAvatar()),
                        image = EmbedImage(url = "attachment://$imageFileName")
                    )
                )

                if (imageResource != null) {
                    data.fileData = ImageIO.read(imageResource).toByteArray().toFileData(imageFileName)
                } else {
                    val missingImageResource = this::class.java.classLoader.getResource("images/missing_image.png")
                    data.fileData = ImageIO.read(missingImageResource).toByteArray().toFileData(imageFileName)
                }

                data
            }
    }
    override fun help(): String {
        return """
            |search - Fuzzy searches a card from the given arguments
            |   e.g. '!card search aduring fun'
            |   which finds 'Adoring Fan' and 'Alduin'
            """.trimMargin()
    }

}

object FindCardCommand: BaseCardCommand() {
    override fun isHidden() = false
    override fun run(args: List<String>, author: User): List<ReplyData> = SearchCardCommand.run(args, author)
    override fun help(): String {
        return "find   - synonym for 'search'"
    }

}

abstract class BaseCardCommand : CardCommand


