package tesl.rest.reader

import tesl.analysis.DeckAnalysis
import tesl.model.Deck
import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.exceptions.BadRequestException
import java.awt.Color
import java.awt.image.BufferedImage
import javax.inject.Named
import javax.inject.Singleton

@Named("deckImageCreator")
@Singleton
class DeckImageCreator: ImageCreator {

    override fun createImage(code: String): ByteArray {
        if (!Decoder(DecoderType.DECK).isCodeValid(code)) throw BadRequestException(message = "Invalid deck code")

        val deck = Deck.importCode(code)
        val a = DeckAnalysis(deck).run { return@run if (totalCards == 0) null else this } ?: throw BadRequestException(message = "No cards in deck")

        val columnCount = 4
        val circleRadius = 28
        val columnGap = 15
        val fullWidth = (300 + circleRadius * 2 + columnGap) * columnCount - columnGap

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE PANELS
        // Create Deck Class Name
        val deckClassNameImage = ImageCreatorHelper.createDeckClassName(a)
        val cardCountImage = ImageCreatorHelper.createCardTotal(a)
        val deckClassIconsImage = ImageCreatorHelper.createDeckClassIcons(a)
        val manaCurveImage = ImageCreatorHelper.createManaCurve(a)
        val classGraphic = ImageCreatorHelper.createClassGraphic(a)
        val statsImage = ImageCreatorHelper.createStats(a)
        val cardsImage = ImageCreatorHelper.createCards(a, columnCount, fullWidth)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE THE MAIN GRAPHICS
        val fullHeight = manaCurveImage.height + cardsImage.height + 10
        val bi = BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_ARGB)
        val g = ImageCreatorHelper.createGraphics(bi)
        g.color = Color.BLACK
        g.fillRect(0, 0, fullWidth, fullHeight)

        // deck class
        val deckWidth = 472 // max(deckClassIconsImage.width, deckClassNameImage.width)
        g.drawImage(deckClassNameImage, (deckWidth - deckClassNameImage.width) / 2, 5, null)
        g.drawImage(cardCountImage, (deckWidth - cardCountImage.width) / 2, deckClassNameImage.height + 5, null)
        g.drawImage(deckClassIconsImage, (deckWidth - deckClassIconsImage.width) / 2, deckClassNameImage.height + cardCountImage.height + 15, null)

        // mana curve
        val manaCurveCentreOffset = (fullWidth - manaCurveImage.width) / 2
        g.drawImage(manaCurveImage, manaCurveCentreOffset, 0, null)

        // class graphic
        val classGraphicOffset = (fullWidth - deckWidth - manaCurveImage.width - classGraphic.width) / 2 + deckWidth + manaCurveImage.width
        g.drawImage(classGraphic, classGraphicOffset, 20, null)

        // stats
        g.drawImage(statsImage, 0, deckClassIconsImage.height + deckClassNameImage.height + cardCountImage.height + 25, null)

        // cards
        g.drawImage(cardsImage, 0, manaCurveImage.height + 10, null)

        // Finally
        g.dispose()
        return bi.toByteArray()
    }

}