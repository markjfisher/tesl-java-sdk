package tesl.bot

object MessageScanner {
    private val scanner = """!\{\{([^\}]*)\}\}""".toRegex()

    fun scanMessage(message: String): List<String> {
        return scanner
            .findAll(message)
            .map { match ->
                match.groups[1]?.value ?: ""
            }
            .toList()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}