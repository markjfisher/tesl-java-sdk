package tesl.stats

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.SystemExitException
import com.xenomachina.argparser.mainBody
import tesl.analysis.DeckAnalysis
import tesl.model.Deck
import tesl.model.Decoder
import tesl.model.DecoderType
import java.io.File
import java.util.*

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
        val canonicalDeckCodes = allCanonicalDeckCodes(logFile)
        val uniqueDeckCodes = canonicalDeckCodes.toHashSet().toList().sorted()

        println("ALL DECKS ANALYSIS")
        showDetails(canonicalDeckCodes)

        println("UNIQUE DECKS ANALYSIS")
        showDetails(uniqueDeckCodes)
    }

    private fun showDetails(deckCodes: List<String>) {
        val classNameToCount = mapOfClassNameToCount(deckCodes)
        val cardToCount = deckCodes.flatMap { Deck.importCode(it).cards }.groupingBy { it }.eachCount()

        val maxClassNameSize = classNameToCount.keys.map { it.length }.max() ?: 0

        val mapByCount = classNameToCount.toList().sortedBy { (_, value) -> value }.reversed().toMap()
        mapByCount.forEach { (className, count) ->
            println(String.format("%-${maxClassNameSize}s : %d", className, count))
        }
        println(String.format("Total %-${maxClassNameSize - 6}s: %d", " ", classNameToCount.values.sum()))

        val cardToCountSorted = cardToCount.toList().sortedBy { (_, value) -> value }.toMap()
        val sortedCardToCountDesc = cardToCountSorted.toList().reversed()
        sortedCardToCountDesc.take(20).toMap().forEach { (card, count) ->
            println(String.format("%-25s: %d", card.name, count))
        }

        println("\nUnique Cards:")
        sortedCardToCountDesc.filter { it.first.unique }.take(20).toMap().forEach { (card, count) ->
            println(String.format("%-25s: %d", card.name, count))
        }

    }

    private fun mapOfClassNameToCount(canonicalDeckCodes: List<String>): SortedMap<String, Int> {
        return canonicalDeckCodes
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
    }

    private fun allCanonicalDeckCodes(logFile: File): List<String> {
        return logFile.readLines()
            .asSequence()
            .filter { it.contains("SP") }
            .mapNotNull { line ->
                val potentialCode = line.substringAfterLast(" ")
                val (valid, unknowns) = Decoder(DecoderType.DECK).checkImportCode(potentialCode)
                if (!valid) {
                    null
                } else {
                    Deck.canonicalCode(potentialCode)
                }
            }
            .toList()
    }
}