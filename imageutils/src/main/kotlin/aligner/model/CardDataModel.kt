package aligner.model

import kotlinx.serialization.Serializable
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

@Serializable
data class SerializableCardData(
    val cardName: String,
    val cardId: String,
    val cardUrl: String,
    val cardColourUrl: String,
    val xOffset: Double,
    val yOffset: Double,
    val scale: Double,
    val index: Int
)

class CardData(
    cardName: String,
    cardId: String,
    cardUrl: String,
    cardColourUrl: String,
    xOffset: Double,
    yOffset: Double,
    scale: Double,
    index: Int
) {
    var cardName by property(cardName)
    fun cardNameProperty() = getProperty(CardData::cardName)

    var cardId by property(cardId)
    fun cardIdProperty() = getProperty(CardData::cardId)

    var cardUrl by property(cardUrl)
    fun cardUrlProperty() = getProperty(CardData::cardUrl)

    var cardColourUrl by property(cardColourUrl)
    fun cardColourUrlProperty() = getProperty(CardData::cardColourUrl)

    var xOffset by property(xOffset)
    fun xOffsetProperty() = getProperty(CardData::xOffset)

    var yOffset by property(yOffset)
    fun yOffsetProperty() = getProperty(CardData::yOffset)

    var scale by property(scale)
    fun scaleProperty() = getProperty(CardData::scale)

    var index by property(index)
    fun indexProperty() = getProperty(CardData::index)
}

class CardDataModel: ItemViewModel<CardData>() {
    val cardName = bind { item?.cardNameProperty() }
    val cardId = bind { item?.cardIdProperty() }
    val cardUrl = bind { item?.cardUrlProperty() }
    val cardColourUrl = bind { item?.cardColourUrlProperty() }
    val xOffset = bind { item?.xOffsetProperty() }
    val yOffset = bind { item?.yOffsetProperty() }
    val scale = bind { item?.scaleProperty() }
    val index = bind { item?.indexProperty() }
}