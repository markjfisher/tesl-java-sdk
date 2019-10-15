package tesl.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import tesl.idgenerator.IdGenerator
import java.io.File
import tesl.model.CardSet as OldCardSet

data class TESLCard(
    val name: String,
    val rarity: String = "",
    val type: String = "",
    val subtypes: List<String> = emptyList(),
    val cost: Int = -1,
    val power: Int = -1,
    val health: Int = -1,
    val set: CardSet = CardSet(id = "", name = ""),
    val collectible: Boolean = false,
    val soulSummon: Int = -1,
    val soulTrap: Int = -1,
    val text: String = "",
    val attributes: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val unique: Boolean = true,
    val imageUrl: String = "",
    var id: String = "",
    var code: String = ""
) {

    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .apply { setSerializationInclusion(JsonInclude.Include.NON_NULL) }

    fun write(file: File) {
        val sanitizedName = sanitize(name)
        id = IdGenerator.generateCardUUID("$sanitizedName-$code")
        mapper.writeValue(file, this)
    }

    companion object {
        fun sanitize(s: String): String {
            var x = s
                .replace("/", "_")
                .replace(" ", "_")
                .replace("'", "_")
                .replace("?", "")
                .toLowerCase()
            var xLen = 0
            while (x.length != xLen) {
                xLen = x.length
                x = x.replace("__", "_")
            }
            return x
        }

        @JvmStatic
        fun copy(card: Card): TESLCard {
            val fixedCard = fixCard(card)

            return TESLCard(
                name = fixedCard.name,
                rarity = fixedCard.rarity,
                type = fixedCard.type,
                subtypes = fixedCard.subtypes,
                cost = fixedCard.cost,
                power = fixedCard.power,
                health = fixedCard.health,
                set = CardSet(name = fixedCard.set.name, id = fixedCard.set.id),
                collectible = fixedCard.collectible,
                soulSummon = fixedCard.soulSummon.toIntOrNull() ?: -1,
                soulTrap = fixedCard.soulTrap.toIntOrNull() ?: -1,
                text = fixedCard.text,
                attributes = fixedCard.attributes,
                keywords = fixedCard.keywords,
                unique = fixedCard.unique,
                imageUrl = fixedCard.imageUrl,
                code = fixedCard.code
            )

        }

        private fun fixCard(card: Card): Card {
            return when (card.name) {
                "Moon Bishop" -> copyCard(card, subtypes = listOf("Khajiit"))
                "Seeker of the Black Arts" -> copyCard(card, subtypes = listOf("Khajiit"))
                "Rebellion General" -> copyCard(card, subtypes = listOf("Khajiit"))
                "Suthay Bootlegger" -> copyCard(card, subtypes = listOf("Khajiit"))
                "Pouncing Senche" -> copyCard(card, subtypes = listOf("Khajiit"))
                "Queen's Captain" -> copyCard(card, subtypes = listOf("Khajiit"))

                "Gravesinger" -> copyCard(card, subtypes = listOf("Imperial"), keywords = emptyList())
                "Cauldron Keeper" -> copyCard(card, subtypes = listOf("Imperial"))

                "Brotherhood Assassin" -> copyCard(card, subtypes = listOf("Dark Elf"))
                "Imposter" -> copyCard(card, subtypes = listOf("Dark Elf"))

                "Master Swordsmith" -> copyCard(card, subtypes = listOf("Redguard"))

                "Vigilant of Stendarr" -> copyCard(card, subtypes = listOf("Breton"))

                "Gavel of the Ordinator" -> copyCard(card, rarity = "Rare", soulSummon = "100", soulTrap = "20")

                "Transmogrify" -> copyCard(card, type = "Action")

                "Duskfang" -> copyCard(card, type = "Item")

                "Bedeviling Scamp" -> copyCard(
                    card,
                    imageUrl = "https://www.legends-decks.com/img_cards/bedevilingscamp.png"
                )
                "Draugr Sentry" -> copyCard(card, imageUrl = "https://www.legends-decks.com/img_cards/draugrsentry.png")

                else -> card
            }
        }

        private fun copyCard(
            card: Card,
            name: String = card.name,
            rarity: String = card.rarity,
            type: String = card.type,
            subtypes: List<String> = card.subtypes,
            cost: Int = card.cost,
            power: Int = card.power,
            health: Int = card.health,
            set: OldCardSet = card.set,
            collectible: Boolean = card.collectible,
            soulSummon: String = card.soulSummon,
            soulTrap: String = card.soulTrap,
            text: String = card.text,
            attributes: List<String> = card.attributes,
            keywords: List<String> = card.keywords,
            unique: Boolean = card.unique,
            imageUrl: String = card.imageUrl,
            id: String = card.id,
            code: String = card.code
        ): Card {
            return Card(
                name = name,
                rarity = rarity,
                type = type,
                subtypes = subtypes,
                cost = cost,
                power = power,
                health = health,
                set = set,
                collectible = collectible,
                soulSummon = soulSummon,
                soulTrap = soulTrap,
                text = text,
                attributes = attributes,
                keywords = keywords,
                unique = unique,
                imageUrl = imageUrl,
                id = id,
                code = code
            )
        }
    }

    data class CardSet(
        val name: String = "",
        val id: String = ""
    )
}

