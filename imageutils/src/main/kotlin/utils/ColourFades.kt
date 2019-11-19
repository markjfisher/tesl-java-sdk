package utils

import tesl.model.DeckClass
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ColourFades {
    private const val width = 300
    private const val height = 52

    private const val fireAlpha = 0.40f
    private val fireEffect = this::class.java.classLoader.getResource("images/fire_transparency.png")
    private val fireImage = ImageIO.read(fireEffect)
    private val fireAlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fireAlpha)

    // the fire floats fade out later than colours so there's some overlap into main image
    private val coloursFadeFloats = floatArrayOf(0.57f, 0.59f, 0.62f)
    private val fireFadeFloats = floatArrayOf(0.58f, 0.64f, 0.72f)

    // The merge points between colours (60% divided by 2 and 3, small buffer either side)
    private val twoColourMergeFloats = floatArrayOf(0.0f, 0.28f, 0.32f, 0.6f) // effectively 0/30/60
    private val threeColourMergeFloats = floatArrayOf(0.0f, 0.18f, 0.22f, 0.38f, 0.42f, 0.6f) // effectively 0/20/40/60

    private val resourcesDir = File("./imageutils/src/main/resources")

    @JvmStatic
    fun main(args: Array<String>) {
        createColourImages()
    }

    private fun createColourImages() {
        DeckClass.values().forEach { deckClass ->
            val colours = deckClass.classAbilities.map { it.classColour.hexColor }

            val image = when (colours.size) {
                1 -> oneColour(colours.first())
                2 -> twoColour(colours)
                3 -> threeColour(colours)
                else -> BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB)
            }
            fadeOut(image, coloursFadeFloats)
            applyFireEffect(image)

            ImageIO.write(image, "PNG", File(resourcesDir, "images/colours/${deckClass.name.toLowerCase()}.png"))
        }
    }

    private fun applyFireEffect(image: BufferedImage): Graphics2D? {
        val fadedFireImage = fadeOut(fireImage, fireFadeFloats)
        val g = image.createGraphics()
        g.composite = fireAlphaComposite
        g.drawImage(fadedFireImage, 0, 0, null)
        g.dispose()
        return g
    }

    private fun oneColour(colour: Color): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()
        g.color = colour
        g.fillRect(0, 0, width, height)
        g.dispose()
        return image
    }

    private fun twoColour(colours: List<Color>): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()

        val grad = LinearGradientPaint(
            Point(0, 0),
            Point(width, height),
            twoColourMergeFloats,
            listOf(colours[0], colours[0], colours[1], colours[1]).toTypedArray()
        )
        g.paint = grad
        g.fillRect(0, 0, width, height)
        g.dispose()
        return image
    }

    private fun threeColour(colours: List<Color>): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = image.createGraphics()

        val grad = LinearGradientPaint(
            Point(0, 0),
            Point(width, height),
            threeColourMergeFloats,
            listOf(colours[0], colours[0], colours[1], colours[1], colours[2], colours[2]).toTypedArray()
        )
        g.paint = grad
        g.fillRect(0, 0, width, height)
        g.dispose()
        return image
    }

    private fun fadeOut(image: BufferedImage, floats: FloatArray): BufferedImage {
        val g = image.createGraphics()
        // fade image to transparent
        val grad = LinearGradientPaint(
            Point(0, 0),
            Point(width, height),
            floats,
            listOf(
                Color(0, 0, 0, 0x00),
                Color(0, 0, 0, 0xa0),
                Color(0, 0, 0, 0xff)
            ).toTypedArray()
        )

        g.paint = grad
        g.composite = AlphaComposite.DstOut
        g.fillRect(0, 0, width, height)

        g.dispose()
        return image
    }

}