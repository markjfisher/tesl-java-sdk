package utils

import tesl.model.DeckClass
import java.awt.Color

object ColourFades {
    fun createColourImages() {
        DeckClass.values().forEach { deckClass ->
            val colours = deckClass.classAbilities.map { it.classColour.hexColor }



            val floats = createTransitionFloats(colours.size, 0.15f)
            val fractions = createColourBoundaryArray(colours)

        }
    }

    private fun createTransitionFloats(n: Int, mergePercent: Float): FloatArray {
        val floats = mutableListOf<Float>()
        floats.add(0.0f)

        when (n) {
            2 -> {
                floats.add(0.5f - mergePercent/2.0f)
                floats.add(0.5f + mergePercent/2.0f)
            }
            3 -> {
                floats.add(0.280f - mergePercent / 2.0f)
                floats.add(0.280f + mergePercent / 4.0f)
                floats.add(0.666f - mergePercent / 2.0f)
                floats.add(0.666f + mergePercent / 4.0f)
            }
        }
        floats.add(1.0f)
        return floats.toFloatArray()
    }

    private fun createColourBoundaryArray(colours: List<Color>) = colours
        .fold(mutableListOf<Color>()) { list, colour ->
            list.add(colour)
            list.add(colour)
            list
        }.toTypedArray()

}