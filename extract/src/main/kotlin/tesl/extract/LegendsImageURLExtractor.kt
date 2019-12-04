package tesl.extract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import krangl.DataFrame
import krangl.readDelim
import org.jsoup.Jsoup
import tesl.model.Card
import tesl.model.CardCache
import java.io.File

object LegendsImageURLExtractor: Extractor() {
    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

    private fun writeCard(card: Card, file: File) {
        mapper.writeValue(file, card)
    }

    private fun safeName(name: String) = name.replace("[^A-Za-z0-9]".toRegex(), "").toLowerCase()

    @JvmStatic
    fun main(args: Array<String>) {
        extractImageLocations()
    }

    private fun extractImageLocations() {
        val resourceDirValue = config[resourceDirKey]
        val resourceDir = File(resourceDirValue)
        if (!resourceDir.exists()) {
            println("ERROR: resource dir $resourceDir does not exist. Please ensure you set this correctly and create it.")
            return
        }

        val ldCodesCSV = this::class.java.getResource("/nameToLDCode.csv").openStream()
        val ldDF = DataFrame.readDelim(ldCodesCSV)
        var canStart = false
        ldDF.rows.forEach { row ->
            val legendsCode = "${row["code"]}"
            val legendsName = row["name"] as String

//            if (legendsName == "dreadclannfear") canStart = true
//            if (!canStart) {
//                println("skipping $legendsName")
//                return@forEach
//            }

            when(legendsName) {
                "dremoraadept" -> println("Reading $legendsName")
                else -> return@forEach
            }

            val rawCardHtml = LegendsDecksExtractor.fetchLDHtml(legendsCode, legendsName)

            val doc = Jsoup.parse(rawCardHtml)
            val img = doc.selectFirst("img[src~=img_cards]").attr("src")
            val element = doc.select(".well_full.margintop")
            val cardData = SparkyCSVCardExtractor.elementToCardData(element)

            val name = cardData["Name"]
            if (name == null) {
                println("Could not read a name for $legendsName, $legendsCode")
                return@forEach
            }

            val fixedName = when(name) {
                "Dremora Channeler" -> "Xivkyn Channeler"
                else -> name
            }
            val matchedCards = CardCache.all().filter { it.name == fixedName }
            if (matchedCards.size > 1) {
                println("ERROR: found multiple cards with this name:\n$matchedCards")
                return@forEach
            }
            if (matchedCards.isEmpty()) {
                println("ERROR: Didn't match card named: $name")
                return@forEach
            }
            val card = matchedCards.first()
            if (card.imageUrl != img) {
                println("saving ${card.name} with new image url: $img")
                val newCard = Card.createFrom(card, imageUrl = img)
                val cardFile = File(resourceDir, "${safeName(newCard.name)}-${card.code}.json")
                writeCard(newCard, cardFile)
            }

        }
    }
}