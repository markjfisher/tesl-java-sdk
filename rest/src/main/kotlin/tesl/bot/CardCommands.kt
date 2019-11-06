package tesl.bot

import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.api.rest.Embed
import com.jessecorbett.diskord.api.rest.EmbedAuthor
import com.jessecorbett.diskord.api.rest.EmbedImage
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.pngAvatar
import me.xdrop.fuzzywuzzy.FuzzySearch
import tesl.model.CardCache

object CardCommands {
    private val allCommands = mapOf(
        "help" to HelpCardCommand,
        "find" to FindCardCommand
    )
    fun find(name: String): CardCommand = allCommands[name] ?: HelpCardCommand
    fun allHelp() = allCommands.values.joinToString("\n") { it.help() }
}

object HelpCardCommand: BaseCardCommand() {
    override fun run(args: List<String>, author: User): List<ReplyData> {
        val helpText = CardCommands.allHelp()
        return listOf(ReplyData(text = listOf("```$helpText```")))
    }

    override fun help(): String {
        return "help - shows this help"
    }
}

object FindCardCommand: BaseCardCommand() {
    private val allCards = CardCache.all()

    override fun run(args: List<String>, author: User): List<ReplyData> {
        if (args.isEmpty()) return listOf(ReplyData(text = listOf("${author.mention} please supply a search term.")))
        val searchTerm = args.joinToString(" ")
        val extractSorted = FuzzySearch.extractSorted(searchTerm, allCards) { it.name }
        if (extractSorted.isEmpty() || extractSorted.first().score < 70) return listOf(ReplyData(text = listOf("${author.mention}, sorry, no matches for $searchTerm.")))

        return extractSorted
            .filter { it.score > 69 }
            .take(3)
            .map {
                ReplyData(
                    text = listOf(""),
                    embed = Embed(
                        title = it.referent.name,
                        description = "Score: ${it.score}",
                        author = EmbedAuthor(name = author.username, authorImageUrl = author.pngAvatar()),
                        image = EmbedImage(url = it.referent.imageUrl)
                    )
                )
            }
    }

    override fun help(): String {
        return "find - Finds a card from the given arguements, e.g. 'find Young Mammoth'"
    }

}

interface CardCommand {
    fun run(args: List<String>, author: User): List<ReplyData>
    fun help(): String
}

abstract class BaseCardCommand() : CardCommand {

}


