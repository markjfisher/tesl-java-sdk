package utils

import aligner.model.SerializableCardData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import tesl.model.CardCache
import java.io.File

object MergeColourToCard {
    private val resourcesDir = File("./imageutils/src/main/resources")

    @JvmStatic
    fun main(args: Array<String>) {
        mergeColourToCard()
    }

    private fun mergeColourToCard() {
        // read the cardData json file
        // get the x/y/scale values and card id
        // get the colour from the attributes of the card

        readCardData().forEach { cardData ->
            val card = CardCache.findById(cardData.cardId) ?: return@forEach


        }
    }

    private fun readCardData(): List<SerializableCardData> {
        val json = Json(JsonConfiguration.Stable)
        val cardDataFile = File(resourcesDir, "cardData.json")
        val jsonString = cardDataFile.readText()

        val serializableCardData = json.parse(SerializableCardData.serializer().list, jsonString)
        return serializableCardData
    }


}