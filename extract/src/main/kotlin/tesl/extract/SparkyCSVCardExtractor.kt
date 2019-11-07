package tesl.extract

import krangl.DataFrame
import krangl.DataFrameRow
import krangl.eq
import krangl.readDelim
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import tesl.oldmodel.TESLCard
import java.io.File

object SparkyCSVCardExtractor: Extractor() {

    @JvmStatic
    fun main(args: Array<String>) {
        val resourceDirValue = config[resourceDirKey]
        val resourceDir = File(resourceDirValue)
        if (!resourceDir.exists()) {
            println("ERROR: resource dir $resourceDir does not exist. Please ensure you set this correctly and create it.")
            return
        }

        val sparkyPantsCodeCSV = this::class.java.getResource("/allCards.csv").openStream()
        val sparkyPantsDF = DataFrame
            .readDelim(sparkyPantsCodeCSV)
            .filter { it["card_source"] eq "Normal" }
            // Additional filters, e.g.
            // .filter { it["collection"] eq "Crisis" }
            // .filterByRow { (it["name"] as String).contains(',') }

        val legendsAllNameToCodeCSV = this::class.java.getResource("/nameToLDCode.csv").openStream()
        val legendsAllDF = DataFrame.readDelim(legendsAllNameToCodeCSV)

        sparkyPantsDF.rows.forEach lit@{ row: DataFrameRow ->
            // Try and match sparkyPants data with a legends entry
            val legendsName = sparkyNameToLegendsDecksName(shortName(row["name"] as String))
            val legendsDataFilterDF = legendsAllDF.filter { x -> x["name"] eq legendsName }

            if (legendsDataFilterDF.rows.count() == 0) {
                println("***** Error finding match for $legendsName in legends list")
                return@lit
            }

            val legendsCode = (legendsDataFilterDF["code"][0] as Int).toString()
            // println("processing ${row["name"]} -> $legendsCode/$legendsName")

            // It doesn't matter that SP name can be wrong, as we read all resources in, the actual data will be correct inside.
            val spCode = row["export_code"] as String
            val spName = row["name"] as String // Some of these are wrong
            val sanitizedName = TESLCard.sanitize(spName)
            val cardName = "${sanitizedName}-${spCode}.json"
            val cardFile = File(resourceDir, cardName)
            if (cardFile.exists()) {
                return@lit
            }
            println("Didn't find $cardFile, so fetching")

            // FETCH HTML PHASE
            val rawCardHtml = fetchLDHtml(legendsCode, legendsName)

            // PARSING PHASE
            val doc = Jsoup.parse(rawCardHtml)
            val img = doc.selectFirst("img[src~=img_cards]").attr("src")

            val element = doc.select(".well_full.margintop")
            val cardData = elementToCardData(element)

            // SAVING PHASE
            val card = createCard(cardData, row["export_code"] as String, img, setMap[row["collection"]])
            // println ("Would have saved $card as $cardName to $cardFile")
            card.write(cardFile)

        }

    }

    private fun sparkyNameToLegendsDecksName(s: String): String {
        // for problem names, map from the Sparkypants Name to Legends Name
        // Some misspellings on legends side, others are different names in sparky list to their ingame name (e.g. Burma Oppressor).
        return when (s) {
            "brotherhoodsuspect" -> "brotherhoodsupect" // misspell in legends
            "hannibaltraven" -> "hannibaltravel" // misspell in legends
            "imperialsaboteur" -> "brumaoppressor" // renamed in game?
            "cadwellcadwell" -> "cadwellthesoulshrivencadwellthebetrayer" // shortname in game
            "iliacsorceror" -> "iliacsorcerer" // probably renamed in release, legends is correct
            "rayvatthemagetavyartheknight" -> "tavyartheknightrayvatthemage" // wrong way around
            "rebelliousgeneral" -> "rebelliongeneral" // probably renamed in release, legends is correct
            "sencherahtgraveprowler" -> "senchegraveprowler" // extension got dropped from game version
            else -> s
        }
    }

}

