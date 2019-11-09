package tesl.rest.reader

import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.exceptions.BadRequestException
import java.awt.Color
import java.awt.image.BufferedImage
import javax.inject.Named
import javax.inject.Singleton
import tesl.model.Collection as TESLCollection

@Named("collectionImageCreator")
@Singleton
class CollectionImageCreator: ImageCreator {
    override fun createImage(code: String): ByteArray {
        if (!Decoder(DecoderType.COLLECTION).isCodeValid(code)) throw BadRequestException(message = "Invalid collection code")

        val deck = TESLCollection.importCode(code)
        val a = DeckAnalysis(deck).run { return@run if (totalCards == 0) null else this } ?: throw BadRequestException(message = "No cards in collection")

        val columnCount = 20
        val fullWidth = 365 * columnCount - 15

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE PANELS
        val manaCurveImage = ImageCreatorHelper.createManaCurve(a)
        val statsImage = ImageCreatorHelper.createStats(a)
        val cardsImage = ImageCreatorHelper.createCards(a, columnCount, fullWidth)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE THE MAIN GRAPHICS
        val fullHeight = manaCurveImage.height + cardsImage.height + 10
        val bi = BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_ARGB)
        val g = ImageCreatorHelper.createGraphics(bi)
        g.color = Color.BLACK
        g.fillRect(0, 0, fullWidth, fullHeight)

        // mana curve
        val manaCurveCentreOffset = (fullWidth - manaCurveImage.width) / 2
        g.drawImage(manaCurveImage, manaCurveCentreOffset, 0, null)

        // stats
        g.drawImage(statsImage, 0, 20, null)

        // cards
        g.drawImage(cardsImage, 0, manaCurveImage.height + 10, null)

        // Finally
        g.dispose()
        return bi.toByteArray()
    }

}