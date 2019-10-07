package tesl.resources

import java.io.File
import java.io.InputStream

internal object Resources

fun resourceStream(name: String): InputStream {
    return Resources.javaClass.getResourceAsStream(name)
}

fun resourceFiles(path: String): List<File> = Resources
    .javaClass
    .getResource(path)
    .path.let { (File(it).listFiles() ?: emptyArray()).toList() }