package tesl.model

class Collection(
    cards: List<Card> = emptyList()
): CardGrouping(cards) {

    fun exportCode(): String {
        return Decoder(DecoderType.COLLECTION).createExportCode(this)
    }

    companion object {
        @JvmStatic
        fun importCode(code: String) = Collection(Decoder(DecoderType.COLLECTION).createListFromCode(code))

        @JvmStatic
        fun canonicalCode(code: String) = importCode(code).exportCode()
    }
}
