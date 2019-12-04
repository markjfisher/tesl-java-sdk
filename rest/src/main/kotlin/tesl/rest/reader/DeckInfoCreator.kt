package tesl.rest.reader

import tesl.analysis.DeckAnalysis
import tesl.model.Deck
import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.exceptions.BadRequestException
import tesl.rest.model.CardInfo
import tesl.rest.model.CardInfoCount
import tesl.rest.model.DeckInfo
import javax.inject.Singleton

@Singleton
class DeckInfoCreator {
    fun parse(code: String): DeckInfo {
        if (!Decoder(DecoderType.DECK).isCodeValid(code)) throw BadRequestException(message = "Invalid deck code")

        val deck = Deck.importCode(code)
        val analysis = DeckAnalysis(deck)
        val cardsSummary = deck.cards
            .sortedBy { it.name }
            .groupBy { it.name }
            .map { (_, typeCards) ->
                val card = typeCards.first()
                CardInfoCount(
                    count = typeCards.size,
                    card = CardInfo(
                        id = card.id,
                        name = card.name,
                        rarity = card.rarity,
                        mana = card.cost,
                        power = card.power,
                        health = card.health,
                        type = card.type,
                        attributes = card.attributes,
                        keywords = card.keywords
                    )
                )
            }

        return DeckInfo(
            code = code,
            className = analysis.className,
            commonCount = analysis.commonCount,
            rareCount = analysis.rareCount,
            epicCount = analysis.epicCount,
            legendaryCount = analysis.legendaryCount,
            soulgemCost = analysis.soulgemCost,
            creatureCount = analysis.creatureCount,
            actionCount = analysis.actionCount,
            itemCount = analysis.itemCount,
            supportCount = analysis.supportCount,
            cards = cardsSummary
        )
    }

}
