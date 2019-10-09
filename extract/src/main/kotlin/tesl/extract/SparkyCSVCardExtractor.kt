package tesl.extract

import kong.unirest.Unirest
import krangl.*
import org.jsoup.Jsoup
import tesl.model.TESLCard

class SparkyCSVCardExtractor {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val sparkyPantsCodeCSV = this::class.java.getResource("/allCards.csv").openStream()
            val sparkyPantsDF = DataFrame
                .readDelim(sparkyPantsCodeCSV)
                .filter { it["card_source"] eq "Normal" }
                .filterByRow { (it["name"] as String).contains(',') }

            // val oblivionDF = sparkyPantsDF.filter { it["collection"] eq "Crisis" }
            // val jooCSVData = this::class.java.getResource("/nameToLDCode-joo.csv").openStream()
            // val jooLegendsDecksDF = DataFrame.readDelim(jooCSVData)

            val legendsAllNameToCodeCSV = this::class.java.getResource("/nameToLDCode.csv").openStream()
            val legendsAllDF = DataFrame.readDelim(legendsAllNameToCodeCSV)

            sparkyPantsDF.rows.forEach lit@{ row: DataFrameRow ->
                // Try and match sparkyPants data with a legends entry
                val legendsName = sparkyNameToLegendsDecksName(shortName(row["name"] as String))
                val legendsDataFilterDF = legendsAllDF.filter { x -> x["name"] eq legendsName }

                if (legendsDataFilterDF.rows.count() == 0) {
                    println("Error finding match for $legendsName in legends list")
                    return@lit
                }

                val legendsCode = legendsDataFilterDF["code"][0] as Int
                println("processing ${row["name"]} -> $legendsCode/$legendsName")

                // FETCH HTML PHASE
                val x = Unirest
                    .get("https://www.legends-decks.com/card/{code}/{name}")
                    .routeParam("code", legendsCode.toString())
                    .routeParam("name", legendsName)
                    .headers(defaultHeaders)
                    .asString()
                    .body

                // PARSING PHASE
                val doc = Jsoup.parse(x)
                val img = doc.selectFirst("img[src~=img_cards]").attr("src")

                val cardData = doc.select(".well_full.margintop")
                    .select("table")
                    .select("tbody")
                    .first()
                    .getElementsByTag("tr")
                    .map { elem ->
                        val tds = elem.getElementsByTag("td")
                        val key = tds[0].text()
                        val value = when (key) {
                            "Attributes" -> tds[1].getElementsByTag("img").joinToString(",") { e -> e.attr("alt") }
                            "Keywords" -> tds[1].text().toLowerCase()
                            else -> tds[1].text()
                        }
                        key to value
                    }
                    .toMap()

                // SAVING PHASE
                val card = createCard(cardData, row["export_code"] as String, img, setMap[row["collection"]])
                card.write("/tmp/cards-all")

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

        private val setMap = mapOf(
            "Crisis" to TESLCard.CardSet(name = "Jaws of Oblivion", id = "joo"),
            "Alliance_War" to TESLCard.CardSet(id = "aw", name = "Alliance War"),
            "0_Core" to TESLCard.CardSet(id = "cs", name = "Core Set"),
            "Forgotten_Hero" to TESLCard.CardSet(id = "fhc", name = "Forgotten Hero Collection"),
            "Frostfall" to TESLCard.CardSet(id = "fsc", name = "FrostSpark Collection"),
            "Skyrim" to TESLCard.CardSet(id = "hos", name = "Heroes of Skyrim"),
            "Morrowind" to TESLCard.CardSet(id = "hom", name = "Houses of Morrowind"),
            "Isles_Of_Madness" to TESLCard.CardSet(id = "iom", name = "Isle of Madness"),
            "Madhouse_Collection" to TESLCard.CardSet(id = "mc", name = "Madhouse Collection"),
            "Monthlies" to TESLCard.CardSet(id = "mr", name = "Monthly Rewards"),
            "Sage" to TESLCard.CardSet(id = "moe", name = "Moons of Elsweyr"),
            "Festival_Of_Madness" to TESLCard.CardSet(id = "pc", name = "Promotional Cards"),
            "Clockwork_City" to TESLCard.CardSet(id = "rcc", name = "Return to Clockwork City"),
            "Dark_Brotherhood" to TESLCard.CardSet(id = "fodb", name = "The Fall of the Dark Brotherhood")
        )

        private fun shortName(s: String) = s
            .replace("-", "")
            .replace("'", "")
            .replace("?", "")
            .replace("/", "")
            .replace("\"", "")
            .replace(",", "")
            .replace(" ", "")
            .toLowerCase()

        private fun createCard(data: Map<String, String>, exportCode: String, img: String, cardSet: TESLCard.CardSet?): TESLCard {
            val name = data.getOrDefault("Name", "UNKNOWN")
            val rarity = data.getOrDefault("Rarity", "UNKNOWN")
            val type = data.getOrDefault("Type", "UNKNOWN")
            val attributes = data.getOrDefault("Attributes", "UNKNOWN").split(",")
            val race = data.getOrDefault("Race", "UNKNOWN")
            val magickaCost = data.getOrDefault("Magicka Cost", "UNKNOWN").toIntOrNull() ?: 0
            val attack = data.getOrDefault("Attack", "UNKNOWN").toIntOrNull() ?: 0
            val health = data.getOrDefault("Health", "UNKNOWN").toIntOrNull() ?: 0
            val soulSummon = data.getOrDefault("Soul Summon", "UNKNOWN").toIntOrNull() ?: 0
            val soulTrap = data.getOrDefault("Soul Trap", "UNKNOWN").toIntOrNull() ?: 0
            val text = data.getOrDefault("Text", "UNKNOWN")
            val keywords = data.getOrDefault("Keywords", "UNKNOWN").split(",")

            return TESLCard(
                name = name,
                rarity = rarity.split(" ").first(),
                type = type,
                subtypes = listOf(race).filter { it.isNotEmpty() },
                cost = magickaCost,
                power = attack,
                health = health,
                set = cardSet ?: TESLCard.CardSet(id = "__", name = "UNKNOWN_SET"),
                collectible = true,
                soulSummon = soulSummon,
                soulTrap = soulTrap,
                text = text,
                attributes = attributes.map { it.capitalize() }.filter { it.isNotEmpty() },
                keywords = keywords.map { it.capitalize() }.filter { it.isNotEmpty() },
                unique = rarity.contains("Unique"),
                imageUrl = img,
                code = exportCode
            )
        }

        private val defaultHeaders = mapOf(
            "origin" to "https://www.legends-decks.com",
            "accept-encoding" to "gzip, deflate, br",
            "accept-language" to "en-GB,en;q=0.9,en-US;q=0.8,fr;q=0.7",
            "x-requested-with" to "XMLHttpRequest",
            "pragma" to "no-cache",
            "content-type" to "application/x-www-form-urlencoded; charset=UTF-8",
            "accept" to "application/json, text/javascript, */*; q=0.01",
            "cache-control" to "no-cache",
            "authority" to "www.legends-decks.com",
            "referer" to "https://www.legends-decks.com/deck-builder"
        )

    }

}

