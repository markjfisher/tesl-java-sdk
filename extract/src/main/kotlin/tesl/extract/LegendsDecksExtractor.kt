package tesl.extract

import krangl.DataFrame
import krangl.readDelim
import org.jsoup.Jsoup
import tesl.model.CardCache
import tesl.oldmodel.TESLCard
import java.io.File

object LegendsDecksExtractor: Extractor() {
    @JvmStatic
    fun main(args: Array<String>) {
        // Take a CSV list of LD code/IDs
        // For each: Read card data from LD
        //   Look for this card name in our cards
        //   Save if not found
        val resourceDirValue = config[resourceDirKey]
        val resourceDir = File(resourceDirValue)
        if (!resourceDir.exists()) {
            println("ERROR: resource dir $resourceDir does not exist. Please ensure you set this correctly and create it.")
            return
        }

        val ldCodesCSV = this::class.java.getResource("/nameToLDCode-non-collectible.csv").openStream()
        val ldDF = DataFrame
            .readDelim(ldCodesCSV)

        ldDF.rows.forEach { row ->
            val legendsCode = "${row["code"]}"
            val legendsName = row["name"] as String
            // Ran it once, and only these 3 cards were missing (takes an age to loop)
            when(legendsName) {
                "avatarofakatosh", "blackwoodhoodlum", "paintedtroll" -> println("Reading $legendsName")
                else -> return@forEach
            }
            val rawCardHtml = fetchLDHtml(legendsCode, legendsName)

            // PARSING PHASE
            val doc = Jsoup.parse(rawCardHtml)
            val img = doc.selectFirst("img[src~=img_cards]").attr("src")
            val element = doc.select(".well_full.margintop")
            val cardData = SparkyCSVCardExtractor.elementToCardData(element)

            val name = cardData["Name"]
            if (name == null) {
                println("Could not read a name for $legendsName, $legendsCode")
                return@forEach
            }

            val matchedCards = CardCache.all().filter { it.name == name }
            if (matchedCards.size > 1) {
                println("ERROR: found multiple cards with this name:\n$matchedCards")
                return@forEach
            }
            if (matchedCards.size == 1) {
                return@forEach
            }
            val card = createCard(cardData, "__", img, TESLCard.CardSet(name = "TODO", id = "TODO"))

            val sanitizedName = TESLCard.sanitize(name)
            val cardName = "${sanitizedName}-__.json"
            val cardFile = File(resourceDir, cardName)
            if (cardFile.exists()) {
                println("Found card at $cardFile - skipping write")
                return@forEach
            }

            card.write(cardFile)

        }
    }

}