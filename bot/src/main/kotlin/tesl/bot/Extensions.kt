package tesl.bot

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

fun BufferedImage.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "PNG", baos)
    return baos.toByteArray()
}
