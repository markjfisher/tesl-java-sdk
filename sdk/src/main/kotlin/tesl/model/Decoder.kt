package tesl.model

open class Decoder(
    val type: DecoderType
) {

    // Converts a number to a marker.
    // For a Deck, it uses base 26 rooted at A for 0, ie. AA=0, AB=1, ... AZ=25, BA=26, ...
    // For a Collection, base 90 rooted at ! for 0
    private fun createCountMaker(length: Int): String {
        val low = (length % type.base + type.zeroChar.toInt()).toChar()
        val high = (length / type.base + type.zeroChar.toInt()).toChar()
        return "$high$low"
    }

    fun checkImportCode(code: String): Pair<Boolean, List<String>> {
        if (!isCodeValid(code)) return Pair(false, emptyList())

        val unknowns = mutableListOf<String>()

        val codeSeq = SeqOfString(code)
        codeSeq.take(2)

        unknowns.addAll(findUnknownCodes(codeSeq)) // of 1
        unknowns.addAll(findUnknownCodes(codeSeq)) // of 2
        unknowns.addAll(findUnknownCodes(codeSeq)) // of 3

        return Pair(true, unknowns)
    }

    private fun findUnknownCodes(seq: SeqOfString): List<String> {
        val count = decodeCountMarker(seq.take(2))
        val items = mutableListOf<String>()
        repeat(0.until(count).count()) {
            val code = seq.take(2)
            if (CardCache.findByCode(code) == null) {
                items.add(code)
            }
        }
        return items
    }


    fun createListFromCode(code: String): List<Card> {
        // Minimum decodable deck string: SPAAAAAA, which is an empty deck.
        // Deck length must be even as everything is split into pairs of chars
        if (!isCodeValid(code)) return emptyList()
        val cards = mutableListOf<Card>()

        val codeSeq = SeqOfString(code)
        codeSeq.take(2)

        cards.addAll(convertSeqToListOfCards(1, codeSeq))
        cards.addAll(convertSeqToListOfCards(2, codeSeq))
        cards.addAll(convertSeqToListOfCards(3, codeSeq))

        return cards
    }

    private fun convertSeqToListOfCards(of: Int, seq: SeqOfString): List<Card> {
        val count = decodeCountMarker(seq.take(2))
        val items = mutableListOf<Card>()
        repeat(0.until(count).count()) {
            val code = seq.take(2)
            val card = CardCache.findByCode(code)
            val x = MutableList(of) { card }.mapNotNull { it }
            items.addAll(x)
        }
        return items
    }

    fun decodeCountMarker(code: String): Int {
        val low = code[1].toInt() - type.zeroChar.toInt()
        val high = (code[0].toInt() - type.zeroChar.toInt()) * type.base
        return low + high
    }


    fun isCodeValid(code: String): Boolean {
        if (!code.startsWith("SP")) return false
        if (code.length < 8) return false

        val of1MarkerIndex = 2
        val of1Count = decodeCountMarker(code.substring(intRange(of1MarkerIndex)))
        if (code.length < (4 + of1Count * 2 + 4)) return false

        val of2MarkerIndex = 4 + of1Count * 2
        val of2Count = decodeCountMarker(code.substring(intRange(of2MarkerIndex)))
        if (code.length < (6 + of1Count * 2 + of2Count * 2 + 2)) return false

        val of3MarkerIndex = 6 + of1Count * 2 + of2Count * 2
        val of3Count = decodeCountMarker(code.substring(intRange(of3MarkerIndex)))
        if (code.length != (8 + of1Count * 2 + of2Count * 2 + of3Count * 2)) return false

        return true
    }

    private fun intRange(start: Int, end: Int = start + 1) = IntRange(start, end)

    fun createExportCode(group: CardGrouping): String {
        return "SP" +
                (1..3).joinToString("") { i ->
                    createCountMaker(group.of(i).size) + group.of(i).sortedBy { it.code }.joinToString("") { it.code }
                }

    }

    private class SeqOfString(s: String) {
        private var seq = s.asSequence()

        fun take(n: Int): String {
            val s = seq.take(n).joinToString("")
            seq = seq.drop(2)
            return s
        }
    }
}

enum class DecoderType(val base: Int, val zeroChar: Char) {
    DECK(26, 'A'),
    COLLECTION(90, '!')
}