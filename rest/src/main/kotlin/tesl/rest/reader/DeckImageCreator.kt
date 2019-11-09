package tesl.rest.reader

import tesl.model.Deck
import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.exceptions.BadRequestException
import java.awt.Color
import java.awt.image.BufferedImage
import javax.inject.Singleton

@Singleton
class DeckImageCreator: BaseImageCreator() {
    override fun columnCount() = 4

    // Deck Class       |    Mana    |   Class  |
    // Deck Icons       |   Curve    | cardback |
    // 2 x 5 Stats      |            |          |

    // Done as panels of individual images
    // Brought together pasting into final image

    override fun createImage(code: String): ByteArray {
        if (!Decoder(DecoderType.DECK).isCodeValid(code)) throw BadRequestException(message = "Invalid deck code")

        val deck = Deck.importCode(code)
        val a = DeckAnalysis(deck).run { return@run if (totalCards == 0) null else this } ?: throw BadRequestException(message = "No cards in deck")

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE PANELS
        // Create Deck Class Name
        val deckClassNameImage = createDeckClassName(a)
        val deckClassIconsImage = createDeckClassIcons(a)
        val manaCurveImage = createManaCurve(a)
        val classGraphic = createClassGraphic(a)
        val statsImage = createStats(a)
        val cardsImage = createCards(a)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE THE MAIN GRAPHICS
        val fullHeight = manaCurveImage.height + cardsImage.height + 10
        val bi = BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)
        g.color = Color.BLACK
        g.fillRect(0, 0, fullWidth, fullHeight)

        // deck class
        val deckWidth = 472 // max(deckClassIconsImage.width, deckClassNameImage.width)
        g.drawImage(deckClassNameImage, (deckWidth - deckClassNameImage.width) / 2, 5, null)
        g.drawImage(deckClassIconsImage, (deckWidth - deckClassIconsImage.width) / 2, deckClassNameImage.height + 20, null)

        // mana curve
        val manaCurveCentreOffset = (fullWidth - manaCurveImage.width) / 2
        g.drawImage(manaCurveImage, manaCurveCentreOffset, 0, null)

        // class graphic
        val classGraphicOffset = 472 + 500 + 86
        g.drawImage(classGraphic, classGraphicOffset, 20, null)

        // stats
        g.drawImage(statsImage, 0, deckClassIconsImage.height + deckClassNameImage.height + 40, null)

        // cards
        g.drawImage(cardsImage, 0, manaCurveImage.height + 10, null)

        // Finally
        g.dispose()
        return bi.toByteArray()
    }

}