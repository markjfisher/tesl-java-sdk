package utils

import com.mortennobel.imagescaling.ResampleOp
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CircleScaler {
    private val inputResourcesDir = File("./imageutils/src/main/resources")
    private val outputResourcesDir = File("./rest/src/main/resources")

    @JvmStatic
    fun main(args: Array<String>) {
        createCircles(56)
    }

    private fun createCircles(size: Int) {
        val originalList = listOf(
            "agility-239.png",
            "endurance-239.png",
            "intelligence-239.png",
            "neutral-239.png",
            "outer-239.png",
            "outer-trans-239.png",
            "outer-blue-239.png",
            "strength-239.png",
            "willpower-239.png"
        )

        originalList.forEach { pngName ->
            val inputImage = ImageIO.read(File(inputResourcesDir, "images/$pngName"))
            val scaledImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            val scaleOperation = ResampleOp(size, size)
            scaleOperation.filter(inputImage, scaledImage)
            val name = pngName.substringBefore("-239.png")
            ImageIO.write(scaledImage, "PNG", File(outputResourcesDir, "images/${name}.png"))
        }
    }
}