package tesl.bot

import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.api.rest.CreateMessage
import com.jessecorbett.diskord.api.rest.Embed
import com.jessecorbett.diskord.api.rest.EmbedAuthor
import com.jessecorbett.diskord.api.rest.EmbedImage
import com.jessecorbett.diskord.api.rest.client.ChannelClient
import com.jessecorbett.diskord.api.websocket.events.Ready
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.pngAvatar
import com.jessecorbett.diskord.util.sendFile
import com.jessecorbett.diskord.util.words
import com.natpryce.konfig.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult
import mu.KotlinLogging
import tesl.model.Card
import tesl.model.CardCache
import tesl.model.Decoder
import tesl.model.DecoderType

private val logger = KotlinLogging.logger {}

object BotCheck {

    private val token = Key("tesl.bot.token", stringType)
    private val cmdPostFix = Key("tesl.bot.cmd-post-fix", stringType)

    private val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("tesl-bot.properties")

    @UnstableDefault
    @JvmStatic
    fun main(args: Array<String>) {
        // a while loop here doesn't help. eats all resources on machine, as bot doesn't release gracefully.
        try {
            runBot()
        } catch (e: Exception) {
            logger.error(e) { "Bot died :(" }
        }

    }

    private fun runBot() {
        val token = config[token]
        val postFix = config[cmdPostFix]
        logger.info { "Bot running, post fix = '$postFix'" }
        runBlocking {
            bot(token) {
                commands(prefix = "!") {
                    command(command = "deck$postFix") {
                        reply(channel, doDeckCommand(words, author))
                    }
                    command(command = "card$postFix") {
                        doCardCommand(words, author).forEach {
                            if (it.fileData == null) {
                                reply(channel, it)
                            } else {
                                channel.createMessage(
                                    CreateMessage(
                                        content = it.text.first(),
                                        embed = it.embed
                                    ),
                                    it.fileData!!
                                )
                            }
                        }
                    }
                }

                started { ready: Ready ->
                    logger.info { "started with sessionId: ${ready.sessionId}" }
                    val guilds = ready.guilds
                        .map { g -> g.id }
                        .map { id -> clientStore.guilds[id].get() }

                    logger.info { "Registered with following guilds" }
                    guilds.forEach { guild ->
                        val owner = clientStore.discord.getUser(guild.ownerId)
                        logger.info { " ${guild.name}, owner: ${owner.username}, region: ${guild.region}" }
                    }
                }

                messageCreated { message ->
                    if (message.content.contains("!{{")) {
                        val replies = MessageScanner.scanMessage(message.content).mapNotNull { match ->
                            val isDeckCode = Decoder(DecoderType.DECK).checkImportCode(match).first
                            when {
                                isDeckCode -> doDeckCommand(listOf("!deck", "image", match), message.author)
                                else -> matchSearch(match, message.author)
                            }
                        }
                        replies.forEach {
                            reply(message.channel, it)
                        }
                    }
                }
            }
        }
    }

    private fun matchSearch(match: String, author: User): ReplyData? {
        val searchResults = FuzzySearch.extractTop(match, CardCache.all(), { it.name }, 1, 72)
        return when {
            searchResults.isNotEmpty() -> replyForFirstResult(searchResults.first().referent, author)
            else -> null
        }
    }

    private fun replyForFirstResult(card: Card, author: User): ReplyData {
        return ReplyData(
            text = listOf(""),
            embed = Embed(
                title = card.name,
                author = EmbedAuthor(name = author.username, authorImageUrl = author.pngAvatar()),
                image = EmbedImage(url = card.imageUrl)
            )
        )
    }

    private suspend fun reply(channel: ChannelClient, replyData: ReplyData) {
        if (replyData.fileData != null) {
            channel.sendFile(data = replyData.fileData!!, comment = replyData.text.first())
        } else {
            replyData.text.forEach { text ->
                channel.createMessage(
                    CreateMessage(
                        content = text,
                        embed = replyData.embed
                    )
                )
            }
        }

    }

    private fun doDeckCommand(w: List<String>, author: User): ReplyData {
        val deckArgs = w.drop(1)
        val deckCommand = when {
            deckArgs.isEmpty() -> HelpDeckCommand
            else -> DeckCommands.find(deckArgs[0]) as BaseDeckCommand
        }
        return deckCommand.run(deckArgs.drop(1), author.mention, author.username)
    }

    private fun doCardCommand(w: List<String>, author: User): List<ReplyData> {
        val cardArgs = w.drop(1)
        val cardCommand = when {
            cardArgs.isEmpty() -> HelpCardCommand
            else -> CardCommands.find(cardArgs[0]) as BaseCardCommand
        }
        return cardCommand.run(cardArgs.drop(1), author)
    }
}
