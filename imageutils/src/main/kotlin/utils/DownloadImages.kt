package utils

import tesl.model.CardCache
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

object DownloadImages {
    @JvmStatic
    fun main(args: Array<String>) {
        CardCache.all().forEach { card ->
            val fileName = card.imageUrl.substringAfterLast("/")
            val file = File("/home/markf/dev/personal/gaming/tesl-java-sdk/rest/src/main/resources/images/cards/$fileName")

            if (!file.exists()) {
                println("processing image for ${card.name}")
                val x = ImageIO.read(URL(card.imageUrl))
                ImageIO.write(x, "PNG", file)
            }
        }
    }
}