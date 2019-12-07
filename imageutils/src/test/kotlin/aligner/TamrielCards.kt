package aligner

import aligner.model.SerializableCardData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.junit.jupiter.api.Test

class TamrielCards {
    @Test
    fun `generate serialized card data for tamriel cards`() {
        val tamrielCards = listOf(
            Triple("Aldmeri Spellwright", "Bm", "932f24da-f030-54bc-9bea-b20e5f574711"),
            Triple("Altar of Spellmaking", "AY", "cbdacd40-8d43-55cb-9247-9085aabd46a3"),
            Triple("A New Era", "Be", "6c7044b1-9c5b-5e39-b281-0f20c310b175"),
            Triple("Call Dragon", "Bj", "8b892e6b-e55c-542e-bab2-55d1dac631b6"),
            Triple("Death Scythe", "Bk", "4701e6a2-addf-5fc6-88e0-d0f157a8399c"),
            Triple("Enchanted Ring", "Bc", "834ac9df-6540-59ca-8ef2-c63011d9df68"),
            Triple("Explore", "Bf", "728d4a26-de0b-58de-8984-de830fa50433"),
            Triple("Feed", "Bi", "2d385407-6eec-52c7-b5a5-0ed7ee492cce"),
            Triple("Midnight Burial", "Bn", "6b7b2b1f-f201-53bf-bb0b-8dd93e05a0f7"),
            Triple("Obstinate Goat", "Bg", "25f71646-1ec4-55ef-a0a0-bd297ca25e25"),
            Triple("Ordinator of the Almsivi", "Bb", "4d748dac-33f4-5c43-8349-5e0ff9f873d0"),
            Triple("Pact Outcast", "AX", "59da643d-9113-5099-854e-64911e02fc0b"),
            Triple("Red Mountain", "Ba", "ffc4490e-db4c-504e-99f1-396526606a7a"),
            Triple("Smuggler Underboss", "Bo", "350db23e-9b84-551b-ba8c-eaba00a5f37d"),
            Triple("Strange Brew", "Bl", "33055c68-1c2d-517f-860b-ea8cc343b377"),
            Triple("Wild Echatere", "Bh", "1c66993e-a9c3-594a-9449-15bc3b1d5bc3")
        )

        val serialized = tamrielCards.mapIndexed { i, (name, code, id) ->
            SerializableCardData(
                cardName = name,
                cardId = id,
                cardUrl = "imageutils/src/main/resources/images/cards/${name.toLowerCase().replace(" ", "")}.png",
                cardColourUrl = "imageutils/src/main/resources/images/colours/TODO",
                xOffset = 320.0,
                yOffset = 158.0,
                scale = 0.50,
                index = 1302 + i
            )
        }
        val json = Json(JsonConfiguration.Stable)
        val jsonList = json.stringify(SerializableCardData.serializer().list, serialized)
        println(jsonList)
    }
}