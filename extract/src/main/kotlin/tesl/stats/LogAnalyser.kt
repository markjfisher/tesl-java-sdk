package tesl.stats

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.mainBody
import tesl.model.Deck
import tesl.model.Decoder
import tesl.model.DecoderType
import java.io.File

class LogAnalysisArgs(parser: ArgParser) {
    val source by parser.positional(name = "LOG_FILE", help = "source log file")
}

class LogAnalyser {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            ArgParser(args).parseInto(::LogAnalysisArgs).run {
                val sourceFile = File(source)
                if (!sourceFile.exists()) {
                    throw SystemExitException("log file not found: ${sourceFile.canonicalPath}", -1)
                }
                LogAnalyser().analyse(sourceFile)
            }
        }
    }

    private fun analyse(logFile: File) {
        val canonicalDeckCodes = logFile.readLines().mapNotNull { line ->
            val potentialCode = line.substringAfterLast(" ")
            val (valid, unknowns) = Decoder(DecoderType.DECK).checkImportCode(potentialCode)
            if (!valid) {
                null
            } else {
                Deck.canonicalCode(potentialCode)
            }
        }

        val classNameToCount = canonicalDeckCodes
            .groupingBy { it }.eachCount()
            .map { (code, count) ->
                val a = DeckAnalysis(Deck.importCode(code))
                a.className to count
            }
            .groupBy {
                it.first
            }
            .map { (className, thing) ->
                val total = thing.sumBy { it.second }
                className to total
            }.toMap()
            .toSortedMap()

        val cardToCount = canonicalDeckCodes.flatMap { Deck.importCode(it).cards }.groupingBy { it }.eachCount()
        val cardToCountSorted = cardToCount.toList().sortedBy { (_, value) -> value}.toMap()

        val maxClassNameSize = classNameToCount.keys.map { it.length }.max() ?: 0

        val mapByCount = classNameToCount.toList().sortedBy { (_, value) -> value}.reversed().toMap()
        mapByCount.forEach { (className, count) ->
            println(String.format("%-${maxClassNameSize}s : %d", className, count))
        }
        println("Total: ${classNameToCount.values.sum()}")

        cardToCountSorted.toList().reversed().take(20).toMap().forEach { (card, count) ->
            println(String.format("%-25s: %d", card.name, count))
        }
    }
}