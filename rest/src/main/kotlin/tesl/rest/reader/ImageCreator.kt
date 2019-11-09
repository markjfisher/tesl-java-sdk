package tesl.rest.reader

interface ImageCreator {
    fun createImage(code: String): ByteArray
}
