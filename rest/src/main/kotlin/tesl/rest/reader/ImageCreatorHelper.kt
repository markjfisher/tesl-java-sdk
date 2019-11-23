package tesl.rest.reader

import tesl.model.CardCount
import tesl.model.ClassAbility
import java.awt.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.min

object ImageCreatorHelper {
    private const val fontName = "FreeSans"
    private val leftCircleFilledResource = this::class.java.classLoader.getResource("images/outer-blue.png")
    private val leftCircle = ImageIO.read(leftCircleFilledResource)

    fun calculateColumnLengths(total: Int, columnCount: Int): List<Int> {
        return (0 until columnCount).map { i ->
            total / columnCount + if (i < total % columnCount) 1 else 0
        }
    }

    fun setupToDrawNumber(g: Graphics2D, s: String, colour: Color, style: Int = Font.BOLD, fontSize: Int = 25): Pair<Int, Int> {
        g.font = Font(fontName, style, fontSize)
        val cost = g.fontMetrics
        val wCost = cost.stringWidth(s)
        val hCost = cost.ascent
        g.paint = colour
        return Pair(wCost, hCost)
    }

    fun resize(image: BufferedImage, width: Int, height: Int): BufferedImage {
        val newImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g = newImage.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.dispose()
        return newImage
    }

    fun fileNameFromCardName(s: String)= s.replace("[^A-Za-z0-9]".toRegex(), "").toLowerCase()

    fun createGraphics(image: BufferedImage): Graphics2D = image.createGraphics().apply {
        setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
    }

    fun drawBox(g: Graphics, a: Point, b: Point) {
        g.drawLine(a.x, a.y, b.x, a.y)
        g.drawLine(b.x, a.y, b.x, b.y)
        g.drawLine(b.x, b.y, a.x, b.y)
        g.drawLine(a.x, b.y, a.x, a.y)
    }


    fun createStats(a: DeckAnalysis): BufferedImage {
        // 2 columns of 5 stats
        val bi = BufferedImage(472, 400, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)

        displayDeckDetailValue(g, "Soulgems:", "${a.soulgemCost}", 0, 0)
        displayDeckDetailValue(g, "Creatures:", "${a.creatureCount}", 0, 1)
        displayDeckDetailValue(g, "Actions:", "${a.actionCount}", 0, 2)
        displayDeckDetailValue(g, "Items:", "${a.itemCount}", 0, 3)
        displayDeckDetailValue(g, "Supports:", "${a.supportCount}", 0, 4)

        displayDeckDetailValue(g, "Prophecies:", "${a.prophecyCount}", 1, 0)
        displayDeckDetailValue(g, "Commons:", "${a.commonCount}", 1, 1)
        displayDeckDetailValue(g, "Rares:", "${a.rareCount}", 1, 2)
        displayDeckDetailValue(g, "Epics:", "${a.epicCount}", 1, 3)
        displayDeckDetailValue(g, "Legendaries:", "${a.legendaryCount}", 1, 4)

        return bi
    }

    fun createCardTotal(a: DeckAnalysis): BufferedImage {
        val ofCount = if (a.deckClass.classAbilities.size < 3) 50 else 75
        val total = a.totalCards
        val text = "[$total / $ofCount]"
        val fontSize = 25

        val bi = BufferedImage(200, 80, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)
        g.font = Font(fontName, Font.PLAIN, fontSize)
        val metrics = g.fontMetrics
        val width = metrics.stringWidth(text)
        val height = metrics.ascent + metrics.descent
        g.dispose()

        val bi2 = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2 = createGraphics(bi2)
        g2.font = Font(fontName, Font.PLAIN, fontSize)
        g2.drawString(text, 0, height - metrics.descent)
        g2.dispose()

        return bi2
    }

    fun displayDeckDetailValue(g: Graphics2D, title: String, text: String, x: Int, y: Int) {
        val fontSize = 20

        // Title
        g.font = Font(fontName, Font.PLAIN, fontSize)
        val fmTitle = g.fontMetrics
        val wTitle = fmTitle.stringWidth(title)
        val hTitle = fmTitle.ascent
        g.paint = Color(0x86, 0x86, 0x86)
        val x2 = x * 472 / 2 + (472 / 2) * 7 / 10 - wTitle - 10
        val y2 = hTitle + y * (fontSize * 3 / 2) + 5
        g.drawString(title, x2, y2)

        // Text
        g.font = Font(fontName, Font.BOLD, fontSize)
        // val fmText = g.fontMetrics
        g.paint = Color(0xd2, 0xcb, 0xfe)
        val x3 = x2 + wTitle + 10
        g.drawString(text, x3, y2)

    }

    fun createCards(da: DeckAnalysis, columnCount: Int, fullWidth: Int): BufferedImage {
        val colourLineLight = Color(0x50, 0x4e, 0x36)
        val colourLineDark = Color(0x39, 0x37, 0x25)
        val leftCircleFilledResource = this::class.java.classLoader.getResource("images/outer-blue.png")
        val rightCircleHollowResource = this::class.java.classLoader.getResource("images/outer-trans.png")
        val leftCircle = ImageIO.read(leftCircleFilledResource)

        val heightGap = 8
        val circRadius = 28
        val cardWidth = 300 // ???
        val columnLengths: List<Int> = calculateColumnLengths(da.totalUnique, columnCount)

        val cardsInColumns = mutableListOf<List<CardCount>>()
        var amountLeft = da.cardCountSorted.size
        var currentIndex = 0
        while (amountLeft > 0) {
            amountLeft -= columnLengths[currentIndex]
            val dropCount = (0 until currentIndex).fold(0) { acc, i -> acc + columnLengths[i] }
            val cards = da.cardCountSorted.drop(dropCount).take(columnLengths[currentIndex++])
            cardsInColumns.add(cards)
        }

        val height = columnLengths[0] * (2 * circRadius + heightGap) + 5
        val bi = BufferedImage(fullWidth, height, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)
        cardsInColumns.forEachIndexed { i, list ->
            list.forEachIndexed inner@{ j, (count, card) ->
                if (card == null) return@inner
                val imageX = i * (2 * circRadius + cardWidth + 15) + circRadius
                val imageY = j * (circRadius * 2 + heightGap) + circRadius

                val fileName = fileNameFromCardName(card.name)
                val renderedSource = this::class.java.classLoader.getResource("images/rendered/${fileName}.png")
                val renderedImage = ImageIO.read(renderedSource)
                g.drawImage(renderedImage, imageX, imageY - circRadius + 2, null)

                ////////////////////////////////////////////////////////////////////////////////////
                // CONNECTING PARALLEL LINES (will be overwritten by circles)
                ////////////////////////////////////////////////////////////////////////////////////
                g.color = colourLineDark
                val rightX = imageX + cardWidth // - circRadius * 2
                g.drawLine(imageX, imageY - circRadius + 2, rightX, imageY - circRadius + 2)
                g.drawLine(imageX, imageY + circRadius - 2, rightX, imageY + circRadius - 2)
                g.color = colourLineLight
                g.drawLine(imageX, imageY - circRadius + 1, rightX, imageY - circRadius + 1)
                g.drawLine(imageX, imageY + circRadius - 1, rightX, imageY + circRadius - 1)

                ////////////////////////////////////////////////////////////////////////////////////
                // LEFT CIRCLE
                g.drawImage(leftCircle, imageX - circRadius, imageY - circRadius + 1, null)

                // RIGHT OUTER (OR LINE)
                if (count > 1) {
                    val rightCircle = ImageIO.read(rightCircleHollowResource)
                    g.drawImage(rightCircle, rightX - circRadius, imageY - circRadius + 1, null)
                } else {
                    g.drawLine(rightX, imageY - circRadius + 2, rightX, imageY + circRadius - 2)
                }

                // RIGHT INNER
                if (card.unique) {
                    val uniqueResource = this::class.java.classLoader.getResource("images/unique-28.png")
                    val uniqueImage = ImageIO.read(uniqueResource)
                    g.drawImage(uniqueImage, rightX - 13, imageY - 12, null)
                }

                ////////////////////////////////////////////////////////////////////////////////////
                // LEFT NUMBER = cost
                ////////////////////////////////////////////////////////////////////////////////////
                val costMessage = "${card.cost}"
                val (wCost, hCost) = setupToDrawNumber(g, costMessage, Color.BLACK)
                g.drawString(
                    costMessage,
                    if (costMessage.length == 1) imageX - 7 else imageX - wCost / 2 - 2,
                    imageY + hCost / 2 - 1
                )

                ////////////////////////////////////////////////////////////////////////////////////
                // RIGHT NUMBER = COUNT (if > 1)
                ////////////////////////////////////////////////////////////////////////////////////
                if (count > 1) {
                    val countMessage = "$count"
                    val (wCount, hCount) = setupToDrawNumber(g, countMessage, Color.WHITE)
                    g.drawString(countMessage, rightX + wCount / 2 - 14, imageY + hCount / 2 - 1)
                }

                ////////////////////////////////////////////////////////////////////////////////////
                // NAME
                ////////////////////////////////////////////////////////////////////////////////////
                val nameMessage = card.name.substring(0, min(card.name.length, 23))
                val (_, hNameBlack) = setupToDrawNumber(g, nameMessage, Color.BLACK, Font.PLAIN, 20)
                g.drawString(nameMessage, imageX + 24 + circRadius / 2, imageY + hNameBlack / 2)
                val (_, hName) = setupToDrawNumber(g, nameMessage, Color.WHITE, Font.PLAIN, 20)
                g.drawString(nameMessage, imageX + 22 + circRadius / 2, imageY + hName / 2 - 2)

            }
        }

        return bi
    }

    fun createClassGraphic(a: DeckAnalysis): BufferedImage {
        val bi = BufferedImage(300, 305, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)
        val classNameGraphic = a.className.toLowerCase().replace(" ", "_")
        val classResource = this::class.java.classLoader.getResource("images/class-bg/${classNameGraphic}.png")
        if (classResource != null) {
            val classImage = ImageIO.read(classResource)
            g.drawImage(classImage, 0, 0, null)
        }
        g.dispose()
        return bi
    }

    fun createDeckClassName(a: DeckAnalysis): BufferedImage {
        val bi = BufferedImage(600, 106, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)
        g.font = Font(fontName, Font.PLAIN, 45)
        g.paint = Color(0xd2, 0xcb, 0xfe)

        val cost = g.fontMetrics
        val name = a.className
        val wCost = cost.stringWidth(name)
        val hCost = cost.ascent + cost.descent

        g.drawString(name, 0, cost.ascent - cost.leading)
        g.dispose()

        return resize(bi, wCost, hCost)
    }

    fun createDeckClassIcons(a: DeckAnalysis): BufferedImage {
        val strengthIconResource = this::class.java.classLoader.getResource("images/strength.png")
        val strengthImage = ImageIO.read(strengthIconResource)
        val iconHeight = strengthImage.height

        val neutralCount = a.attributesCount.getOrDefault("Neutral", 0)
        val attributes = a.deckClass.classAbilities.let { if (neutralCount > 0) it + ClassAbility.NEUTRAL else it }
        val width = attributes.size * 100 - if (neutralCount in 1..9) 21 else 6
        val bi = BufferedImage(width, iconHeight, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)

        val fontSize = 30
        g.font = Font(fontName, Font.PLAIN, fontSize)
        g.paint = Color(0xd2, 0xcb, 0xfe)

        attributes.forEachIndexed { attIndex, attribute ->
            val attributeName = attribute.name.toLowerCase()
            val count = a.attributesCount[attributeName.capitalize()]
            val iconResource = this::class.java.classLoader.getResource("images/${attributeName}.png")
            val iconImage = ImageIO.read(iconResource)
            val x = attIndex * 100
            g.drawImage(iconImage, x, 0, null)
            g.drawString("$count", x + iconImage.width + 5, iconImage.height / 2 + fontSize / 2 - 5)
        }

        return bi
    }

    fun createManaCurve(a: DeckAnalysis): BufferedImage {
        val manaFillDark = 0x21a2ff
        val manaFillLight = 0x3fccff
        val manaDarkLine = 0x3169d5
        val manaBoundingBox = 0x16202a
        val manaBackgroundGrey = 0x131516
        val manaBoxWidth = 550
        val manaBoxHeight = 350
        val circRadius = 28

        val bi = BufferedImage(manaBoxWidth, manaBoxHeight, BufferedImage.TYPE_INT_ARGB)
        val g = createGraphics(bi)

        // Fill with background colour
        g.color = Color(manaBackgroundGrey, false)
        g.fillRect(0, 0, manaBoxWidth, manaBoxHeight)

        val largestManaCount = a.manaCurve.values.max() ?: 0
        // draw the 8 circles
        (0..7).forEach { index ->
            ////////////////////////////////////////////////////////////////////////////////////////
            // CIRCLE
            val x = index * (manaBoxWidth / 8) + manaBoxWidth / 16 - circRadius + 2
            val y = manaBoxHeight - circRadius * 2 - 5
            g.drawImage(leftCircle, x, y, null)

            val manaNum = when (index) {
                0, 1, 2, 3, 4, 5, 6 -> "$index"
                else -> "7+"
            }

            val (wCost, hCost) = setupToDrawNumber(g, manaNum, Color.BLACK)
            val numX = if (manaNum.length == 1) x + circRadius - 7 else x - wCost / 2 + circRadius
            val numY = y + hCost / 2 - 2 + circRadius
            g.drawString(manaNum, numX, numY)

            ////////////////////////////////////////////////////////////////////////////////////////
            // MANA BOX OUTLINE AND FILL WITH BLACK
            val outlineBoxWidth = circRadius * 2
            val outlineBoxHeight = manaBoxHeight - circRadius * 2 - 20
            g.color = Color(manaBoundingBox, false)
            drawBox(g, Point(x, 5), Point(x + outlineBoxWidth, outlineBoxHeight + 5))
            g.color = Color.BLACK
            g.fillRect(x + 1, 6, outlineBoxWidth - 1, outlineBoxHeight - 1)

            ////////////////////////////////////////////////////////////////////////////////////////
            // BLUE MANA BOX
            val manaCount = a.manaCurve[index] ?: 0
            if (manaCount > 0 && largestManaCount > 0) {
                val blueBlockHeightMaxHeight = outlineBoxHeight - 1
                val blueBlockHeight = manaCount * blueBlockHeightMaxHeight / largestManaCount

                val blueBlock = BufferedImage(2 * circRadius - 1, blueBlockHeight, BufferedImage.TYPE_INT_ARGB)
                blueBlock.createGraphics().run {
                    paint = LinearGradientPaint(
                        Point(0, 0),
                        Point(0, blueBlockHeight),
                        floatArrayOf(0.0f, 1.0f),
                        listOf(Color(manaFillDark, false), Color(manaFillLight, false)).toTypedArray()
                    )
                    fillRect(0, 0, 2 * circRadius - 1, blueBlockHeight)
                    dispose()
                }

                g.drawImage(blueBlock, x + 1, blueBlockHeightMaxHeight - blueBlockHeight + 6, null)

                // now a small inner border
                g.color = Color(manaDarkLine, false)
                drawBox(g, Point(x + 2, blueBlockHeightMaxHeight - blueBlockHeight + 7), Point(x + circRadius * 2 - 2, outlineBoxHeight + 3))

            }
            // the actual count
            val countOfCurrentMana = "$manaCount"
            val (wManaCount, _) = setupToDrawNumber(g, countOfCurrentMana, Color.WHITE)
            g.drawString(countOfCurrentMana, x - wManaCount / 2 + circRadius, 32)
        }

        return bi
    }

}

fun BufferedImage.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "PNG", baos)
    return baos.toByteArray()
}

