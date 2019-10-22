package tesl.rest.reader

import tesl.model.CardInfo
import tesl.model.DeckInfo
import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.model.*
import javax.inject.Singleton

@Singleton
class DeckReader {
    fun parse(code: String): DeckInfo? {
        if (!Decoder(DecoderType.DECK).isCodeValid(code)) return null

        val deck = Deck.importCode(code)
        val analysis = DeckAnalysis(deck)

        return DeckInfo(
            code = code,
            commonCount = analysis.commonCount,
            rareCount = analysis.rareCount,
            epicCount = analysis.epicCount,
            legendaryCount = analysis.legendaryCount,
            soulgemCost = analysis.soulgemCost,
            cards = deck.cards.map { card ->
                CardInfo(
                    id = card.id,
                    name = card.name,
                    mana = card.cost,
                    power = card.power,
                    health = card.health,
                    type = card.type,
                    attributes = card.attributes,
                    keywords = card.keywords
                )
            }
        )
    }

}
