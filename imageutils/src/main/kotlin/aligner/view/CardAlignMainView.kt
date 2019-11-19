package aligner.view

import aligner.controller.CardAlignController
import aligner.model.CardDataModel
import javafx.scene.Group
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.transform.Scale
import javafx.scene.transform.Translate
import tornadofx.*

class CardAlignMainView : View("Card Align Main View") {

    lateinit var pic: ImageView
    val cardAlignController: CardAlignController by inject()
    val model: CardDataModel by inject()

    val blockX = 10.0
    val blockY = 150.0
    val blockW = 300.0
    val blockH = 52.0

    override val root = gridpane {
        val name = cardAlignController.model.cardName
        row {
            stackpane {
                group {
                    pic = imageview(model.cardUrl) {
                        x = 0.0
                        y = 0.0
                        val scale = Scale(model.scale.value, model.scale.value, 0.0, 0.0)
                        val translate = Translate(model.xOffset.value, model.yOffset.value)
                        transforms.setAll(scale, translate)
                    }

                    imageview(model.cardColourUrl) {
                        x = blockX
                        y = blockY
                    }

                    drawBox(this, blockX, blockY, blockW, blockH)

                }.also {
                    val scaleBy = 1.8
                    val scale = Scale(scaleBy, scaleBy, 0.0, 0.0)
                    it.transforms.setAll(scale)
                }

            }
        }

        shortcut(KeyCombination.valueOf("N")) {
            cardAlignController.nextCard()
        }

        shortcut(KeyCombination.valueOf("P")) {
            cardAlignController.previousCard()
        }

        shortcut(KeyCombination.valueOf("MINUS")) {
            cardAlignController.decreaseCardScale()
        }

        shortcut(KeyCombination.valueOf("EQUALS")) {
            cardAlignController.increaseCardScale()
        }

        shortcut(KeyCombination.valueOf("RIGHT")) {
            cardAlignController.horizontal(1.0)
        }

        shortcut(KeyCombination.valueOf("LEFT")) {
            cardAlignController.horizontal(-1.0)
        }

        shortcut(KeyCombination.valueOf("UP")) {
            cardAlignController.vertical(-1.0)
        }

        shortcut(KeyCombination.valueOf("DOWN")) {
            cardAlignController.vertical(1.0)
        }

        shortcut(KeyCombination.valueOf("SHIFT+RIGHT")) {
            cardAlignController.horizontal(5.0)
        }

        shortcut(KeyCombination.valueOf("SHIFT+LEFT")) {
            cardAlignController.horizontal(-5.0)
        }

        shortcut(KeyCombination.valueOf("SHIFT+UP")) {
            cardAlignController.vertical(-5.0)
        }

        shortcut(KeyCombination.valueOf("SHIFT+DOWN")) {
            cardAlignController.vertical(5.0)
        }

        shortcut(KeyCombination.valueOf("S")) {
            cardAlignController.saveCardData()
        }

        keyboard {
            addEventHandler(KeyEvent.KEY_PRESSED) { println(it.code) }
        }

        prefWidth = 600.0
        prefHeight = 600.0
    }

    private fun drawBox(group: Group, x: Double, y: Double, w: Double, h: Double) {
        with(group) {
            polyline(x, y, x + w, y, x + w, y + h, x, y + h, x, y)
        }
    }

}
