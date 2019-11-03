package tesl.model

class Deck(
    cards: List<Card> = emptyList()
): CardGrouping(cards) {

    fun exportCode(): String {
        return Decoder(DecoderType.DECK).createExportCode(this)
    }

    companion object {
        @JvmStatic
        fun importCode(code: String) = Deck(Decoder(DecoderType.DECK).createListFromCode(code))

        @JvmStatic
        fun canonicalCode(code: String) = importCode(code).exportCode()
    }
}
