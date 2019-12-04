package aligner

import aligner.model.SerializableCardData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.io.File

object ImageUrlFixer {
    private val resourcesDir = File("./imageutils/src/main/resources")

    @JvmStatic
    fun main(args: Array<String>) {
        val json = Json(JsonConfiguration.Stable)
        val cardDataFile = File(resourcesDir, "cardData.json")
        val jsonString = cardDataFile.readText()

        val serializableCardData = json.parse(SerializableCardData.serializer().list, jsonString)

        val newCards = serializableCardData.map {  oldData ->
            val fileName = oldData.cardUrl.substringAfterLast("/").substringBeforeLast(".png").replace("[^A-Za-z0-9]".toRegex(), "").toLowerCase()
            val newCardUrl = oldData.cardUrl.substringBeforeLast("/") + "/" + fileName + ".png"
            val newData = SerializableCardData(
                cardName = oldData.cardName,
                cardId = oldData.cardId,
                cardColourUrl = oldData.cardColourUrl,
                xOffset = oldData.xOffset,
                yOffset = oldData.yOffset,
                scale = oldData.scale,
                index = oldData.index,
                cardUrl = newCardUrl
            )
            newData
        }

        val jsonList = json.stringify(SerializableCardData.serializer().list, newCards)
        val newCardDataFile = File(resourcesDir, "cardData2.json")
        newCardDataFile.writeText(jsonList)
    }
}