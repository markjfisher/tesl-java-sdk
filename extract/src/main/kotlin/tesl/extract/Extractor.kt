package tesl.extract

import com.natpryce.konfig.*
import kong.unirest.Unirest
import org.jsoup.select.Elements
import tesl.oldmodel.TESLCard

open class Extractor {
    val resourceDirKey = Key("tesl.cards.resource-dir", stringType)

    val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("tesl-java-sdk.properties")

    val setMap = mapOf(
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

    fun shortName(s: String) = s
        .replace("-", "")
        .replace("'", "")
        .replace("?", "")
        .replace("/", "")
        .replace("\"", "")
        .replace(",", "")
        .replace(" ", "")
        .toLowerCase()

    fun createCard(
        data: Map<String, String>,
        exportCode: String,
        img: String,
        cardSet: TESLCard.CardSet?
    ): TESLCard {
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

    val defaultHeaders = mapOf(
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

    fun fetchLDHtml(legendsCode: String, legendsName: String): String? {
        return Unirest
            .get("https://www.legends-decks.com/card/{code}/{name}")
            .routeParam("code", legendsCode)
            .routeParam("name", legendsName)
            .headers(LegendsDecksExtractor.defaultHeaders)
            .asString()
            .body
    }

    fun elementToCardData(element: Elements): Map<String, String> {
        return element
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
    }

}
