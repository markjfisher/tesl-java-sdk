package tesl.bot

import com.jessecorbett.diskord.api.model.User
import com.jessecorbett.diskord.api.rest.CreateMessage
import com.jessecorbett.diskord.api.rest.Embed
import com.jessecorbett.diskord.api.rest.EmbedImage
import com.jessecorbett.diskord.api.rest.client.ChannelClient
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.mention
import com.jessecorbett.diskord.util.sendFile
import com.jessecorbett.diskord.util.words
import com.natpryce.konfig.*
import io.micronaut.context.annotation.Requires
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.UnstableDefault
import mu.KotlinLogging
import tesl.Configuration
import tesl.rest.reader.ImageCreator
import javax.inject.Singleton

private val logger = KotlinLogging.logger {}

@Requires(notEnv = ["test"])
@Singleton
class BotCheck(
    private val imageCreator: ImageCreator,
    private val configuration: Configuration
) {

    private val token = Key("tesl.bot.token", stringType)

    private val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("tesl-bot.properties")

    @UnstableDefault
    @EventListener
    fun startBot(e: ServerStartupEvent) {
        // a while loop here doesn't help. eats all resources on machine, as bot doesn't release gracefully.
        try {
            runBot()
        } catch (e: Exception) {
            logger.error(e) { "Bot died :(" }
        }

    }

    private fun runBot() {
        runBlocking {
            bot(config[token]) {
                commands(prefix = "!") {
                    command(command = "deck${configuration.botCommandPostFix}") {
                        reply(channel, doDeckCommand(words, author))
                    }
                    command(command = "card${configuration.botCommandPostFix}") {
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

                started {
                    logger.info { "started with sessionId: ${it.sessionId}" }
                }
            }
        }
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
        deckCommand.imageCreator = imageCreator

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
