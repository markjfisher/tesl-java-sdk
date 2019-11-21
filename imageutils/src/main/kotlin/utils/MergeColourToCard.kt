package utils

import aligner.model.SerializableCardData
import com.mortennobel.imagescaling.ResampleOp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import tesl.model.CardCache
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.roundToInt


object MergeColourToCard {
    private val resourcesDir = File("./imageutils/src/main/resources")
    private val outputResourcesDir = File("./rest/src/main/resources")

    @JvmStatic
    fun main(args: Array<String>) {
        mergeColourToCard()
    }

    private fun mergeColourToCard() {

        readCardData().forEach { cardData ->
            val card = CardCache.findById(cardData.cardId) ?: return@forEach
            // if (cardData.index != 0) return@forEach
            println("doing ${cardData.cardName}")
            val colourImage = ImageIO.read(File(".", cardData.cardColourUrl))

            val cardImage = ImageIO.read(File(".", cardData.cardUrl))

            val scaledX = (cardImage.width * cardData.scale).roundToInt()
            val scaledY = (cardImage.height * cardData.scale).roundToInt()
            val scaledImage = BufferedImage(scaledX, scaledY, BufferedImage.TYPE_INT_ARGB)
            val scaleOperation = ResampleOp(scaledX, scaledY)
            scaleOperation.filter(cardImage, scaledImage)

            // 10, 150 are the coordinates the colour image was set to during the captures
            // The following translates back to coordinates of the image in its relative position.
            val newX = cardData.xOffset * cardData.scale - 10.0
            val newY = cardData.yOffset * cardData.scale - 150.0
            val merge = BufferedImage(300, 52, BufferedImage.TYPE_INT_ARGB)
            val g = merge.createGraphics()
            g.drawImage(scaledImage, newX.toInt(), newY.toInt(), null)
            g.drawImage(colourImage, 0, 0, null)
            g.dispose()

            val fileName = fileNameFromCardName(card.name)
            ImageIO.write(merge, "PNG", File(outputResourcesDir,"images/rendered/${fileName}.png"))

        }
    }

    private fun readCardData(): List<SerializableCardData> {
        val json = Json(JsonConfiguration.Stable)
        val cardDataFile = File(resourcesDir, "cardData.json")
        val jsonString = cardDataFile.readText()

        return json.parse(SerializableCardData.serializer().list, jsonString)
    }

    fun fileNameFromCardName(s: String)= s.replace("[^A-Za-z0-9]".toRegex(), "").toLowerCase()

}