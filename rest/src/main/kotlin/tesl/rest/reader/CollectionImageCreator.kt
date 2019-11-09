package tesl.rest.reader

import tesl.model.Decoder
import tesl.model.DecoderType
import tesl.rest.exceptions.BadRequestException
import java.awt.Color
import java.awt.image.BufferedImage
import javax.inject.Singleton
import tesl.model.Collection as TESLCollection

@Singleton
class CollectionImageCreator: BaseImageCreator() {
    override fun columnCount() = 10

    override fun createImage(code: String): ByteArray {
        if (!Decoder(DecoderType.COLLECTION).isCodeValid(code)) throw BadRequestException(message = "Invalid collection code")

        val deck = TESLCollection.importCode(code)
        val a = DeckAnalysis(deck).run { return@run if (totalCards == 0) null else this } ?: throw BadRequestException(message = "No cards in collection")

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE PANELS
        val manaCurveImage = createManaCurve(a)
        val statsImage = createStats(a)
        val cardsImage = createCards(a)

        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        // CREATE THE MAIN GRAPHICS
        val fullHeight = manaCurveImage.height + cardsImage.height + 10
        val bi = BufferedImage(fullWidth, fullHeight, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)
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