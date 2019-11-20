package aligner.controller

import aligner.model.CardData
import aligner.model.CardDataModel
import aligner.model.SerializableCardData
import aligner.view.CardAlignMainView
import javafx.collections.FXCollections
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import tesl.model.Card
import tesl.model.CardCache
import tesl.model.ClassAbility
import tesl.model.DeckClass
import tornadofx.Controller
import java.io.File

class CardAlignController : Controller() {
    private val mainView: CardAlignMainView by inject()
    private val cards = FXCollections.observableArrayList<CardData>()
    private var currentIndex = 0

    private val cardsDir = File("./rest/src/main/resources/images/cards")
    private val colourBackDir = File("./imageutils/src/main/resources/images/colours")
    private val resourcesDir = File("./imageutils/src/main/resources")

    val model: CardDataModel by inject()

    init {
        println("loading cards and setting initial model")
        CardCache.all().forEachIndexed { i, card ->
            cards.add(CardData(
                cardName = card.name,
                cardId = card.id,
                cardUrl = cardUrl(card),
                cardColourUrl = cardColourUrl(card),
                xOffset = 342.0,
                yOffset = 128.0,
                scale = 0.45,
                index = i
            ))
        }
        model.item = cards[0]
    }

    fun nextCard() {
        currentIndex = if (currentIndex == cards.size - 1) 0 else currentIndex + 1
        model.item = cards[currentIndex]
        sizeIt()
    }

    fun previousCard() {
        currentIndex = if (currentIndex == 0) cards.size - 1 else currentIndex - 1
        model.item = cards[currentIndex]
        sizeIt()
    }

    fun cardUrl(card: Card): String {
        val cardFile = File(cardsDir, card.imageUrl.substringAfterLast("/"))
        return cardFile.toURI().toURL().toString()
    }

    private fun cardColourUrl(card: Card): String {
        val deckClass = deckClassFrom(card)
        val colourFile = File(colourBackDir, "${deckClass.name.toLowerCase()}.png")
        return colourFile.toURI().toURL().toString()
    }

    private fun deckClassFrom(card: Card): DeckClass {
        val allAbilities = card.attributes.toSet().map { ClassAbility.valueOf(it.toUpperCase()) }.toSet() - ClassAbility.NEUTRAL
        return DeckClass.values().find {it.classAbilities.containsAll(allAbilities)} ?: DeckClass.NEUTRAL
    }

    fun decreaseCardScale() {
        model.scale.value -= 0.01
        changeScale()
    }

    fun increaseCardScale() {
        model.scale.value += 0.01
        changeScale()
    }

    fun horizontal(byX: Double) {
        model.xOffset.value += byX
        sizeIt()
    }

    fun vertical(byY: Double) {
        model.yOffset.value += byY
        sizeIt()
    }

    fun saveCardData() {
        println("Saving card data...")
        val json = Json(JsonConfiguration.Stable)
        val serializableCardData = cards.map {
            SerializableCardData(
                cardName = it.cardName,
                cardId = it.cardId,
                cardUrl = toRelativePath(it.cardUrl),
                cardColourUrl = toRelativePath(it.cardColourUrl),
                xOffset = it.xOffset,
                yOffset = it.yOffset,
                scale = it.scale,
                index = it.index
            )
        }
        val jsonList = json.stringify(SerializableCardData.serializer().list, serializableCardData)
        val cardDataFile = File(resourcesDir, "cardData.json")
        cardDataFile.writeText(jsonList)
        println("... saved")
    }

    fun loadCardData() {
        println("Loading card data...")
        val json = Json(JsonConfiguration.Stable)
        val cardDataFile = File(resourcesDir, "cardData.json")
        val jsonString = cardDataFile.readText()

        val serializableCardData = json.parse(SerializableCardData.serializer().list, jsonString)

        val newCardData = serializableCardData.map {
            CardData(
                cardName = it.cardName,
                cardId = it.cardId,
                cardUrl = toUrl(it.cardUrl),
                cardColourUrl = toUrl(it.cardColourUrl),
                xOffset = it.xOffset,
                yOffset = it.yOffset,
                scale = it.scale,
                index = it.index
            )
        }
        cards.clear()
        cards.addAll(newCardData)
        sizeIt()
        println("... done loading")
    }

    fun toRelativePath(url: String?): String {
        if (url == null) return ""
        val urlOfRoot = File(".").toURI().toURL().toString()
        return url.substringAfter(urlOfRoot)
    }

    fun toUrl(s: String): String {
        return File(".", s).toURI().toURL().toString()
    }

    private fun changeScale() {
        model.commit {
            val scale = Scale(model.scale.value, model.scale.value, 0.0, 0.0)
            val transforms = mainView.pic.transforms
            val newTransforms = listOf(scale, transforms.filterIsInstance<Translate>().first())
            mainView.pic.transforms.setAll(newTransforms)
        }
    }

    private fun sizeIt() {
        model.commit {
            val scale = Scale(model.scale.value, model.scale.value, 0.0, 0.0)
            val translate = Translate(model.xOffset.value, model.yOffset.value)
            mainView.pic.transforms.setAll(scale, translate)
        }
        // println("scale: ${model.scale.value}, x: ${model.xOffset.value}, y: ${model.yOffset.value}")
    }

}