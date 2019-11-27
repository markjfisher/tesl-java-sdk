package tesl.extract

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import krangl.DataFrame
import krangl.DataFrameRow
import krangl.eq
import krangl.readDelim
import tesl.idgenerator.IdGenerator
import tesl.model.Card
import tesl.model.CardCache
import tesl.oldmodel.TESLCard
import java.io.File

object AltArtExtractor: Extractor() {
    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .enable(SerializationFeature.INDENT_OUTPUT)
        .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

    @JvmStatic
    fun main(args: Array<String>) {
        createAltVersions()
    }

    private fun createAltVersions() {
        val resourceDirValue = config[resourceDirKey]
        val resourceDir = File(resourceDirValue)
        if (!resourceDir.exists()) {
            println("ERROR: resource dir $resourceDir does not exist. Please ensure you set this correctly and create it.")
            return
        }

        val sparkyPantsCodeCSV = this::class.java.getResource("/allCards.csv").openStream()
        val sparkyPantsDF = DataFrame
            .readDelim(sparkyPantsCodeCSV)
            .filter { it["card_source"] eq "Exclusive" }
            .filterByRow { (it["type_name"] as String).toLowerCase().endsWith("_alt") }

        val allCards = CardCache.all()
        sparkyPantsDF.rows.forEach lit@{ row: DataFrameRow ->
            val name = row["name"] as String
            val exportCode = row["export_code"] as String
            val card = CardCache.findByCode(exportCode)

            if (card == null) {
                val normalCard = allCards.firstOrNull { it.name == name }
                // println("code: $exportCode, name: $name, normal: ${normalCard?.code}")
                if (normalCard != null) {
                    val sanitizedName = TESLCard.sanitize(name)
                    val id = IdGenerator.generateCardUUID("$sanitizedName-$exportCode")
                    // USE SAME imageUrl as original card - not supporting alts other than by code
                    // val imageUrl = normalCard.imageUrl.substringBeforeLast(".png") + "_alt.png"

                    val altCard = Card(
                        name = normalCard.name,
                        rarity = normalCard.rarity,
                        type = normalCard.type,
                        subtypes = normalCard.subtypes,
                        cost = normalCard.cost,
                        power = normalCard.power,
                        health = normalCard.health,
                        set = normalCard.set,
                        collectible = normalCard.collectible,
                        soulSummon = normalCard.soulSummon,
                        soulTrap = normalCard.soulTrap,
                        text = normalCard.text,
                        attributes = normalCard.attributes,
                        keywords = normalCard.keywords,
                        unique = normalCard.unique,
                        isAlt = true,
                        imageUrl = normalCard.imageUrl,
                        id = id,
                        code = exportCode
                    )
                    val cardName = "${sanitizedName}-${exportCode}.json"
                    val cardFile = File(resourceDir, cardName)
                    if (cardFile.exists()) {
                        println("---------- skipping $cardName as it already exists")
                        return@lit
                    }
                    println("writing ${cardFile.canonicalPath}")
                    mapper.writeValue(cardFile, altCard)
                }
            }
        }

    }
}