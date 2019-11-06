package utils

import tesl.model.CardCache
import java.io.File
import java.net.URL
import javax.imageio.ImageIO

object DownloadImages {
    @JvmStatic
    fun main(args: Array<String>) {
        CardCache.all().forEach { card ->
            println("saving image ${card.name}")
            val x = ImageIO.read(URL(card.imageUrl))
            val fileName = card.imageUrl.substringAfterLast("/")
            ImageIO.write(x, "PNG", File("/home/markf/dev/personal/gaming/tesl-java-sdk/rest/src/main/resources/images/cards/$fileName"))
        }
    }
}