package tesl.rest.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.TestCard
import tesl.model.Deck
import tesl.model.DeckClass

class DeckAnalysisTest {
    private val redDeck = Deck.importCode("SPAEcCiPhypuAEsGdzkawiAElDdDmgmV")

    private val aldmeriDominionDeck = Deck.importCode("SPAJxMhfdJgTixwucfdMujADfDkgumAUdIsUxznMswnLoewdpTrCcIvKsyfxlZimjHydnrdh")
    private val daggerfallCovenantDeck = Deck.importCode("SPBHpRzBakrUdVeffVgOkZnboPxGxgmUnHxtyDodqoqtdddwgBigxLctjgrPuCbdsZeUpSAHjExioMjHoRxTehALxqgyxmybxlrCxpxrcrmcxN")
    private val ebonheartPactDeck = Deck.importCode("SPAHrgtQbkxhtwddqcAEgkmypVdpAUmGnAbDgshyoEpeqaaNdynwaMbYmnqkoRqBlIrklx")
    private val empireOfCyrodiilDeck = Deck.importCode("SPAEgToayoeFAKwUcxdfovwVwwwGuqyhbQARxzkknMtsgppTwcfxfDhUlZsmsQimyddhma")
    private val guildswornDeck = Deck.importCode("SPAEgyhahFkrAKfWtmlZuFgjhSjHyhtlnEARmGcxdftioerCsHdLvKhnoocMimydmalYdE")
    private val houseDagothDeck = Deck.importCode("SPADwsguaTAGdqsHhlldoogjAUlDdldImGnMlLvBoeotpTrCvKmCnipZcMdBujbFdh")
    private val houseHlaaluDeck = Deck.importCode("SPANmGrgdSejekajczdXffwWmuyBasABumAUsAerlWbvceebguneaNdLechnuAiqaZhTknlYfAyn")
    private val houseRedoranDeck = Deck.importCode("SPACgOjgAFfWlqrBdLqTAVysjkakcxyFgplLmRovrQxbxEyiytfBwEeDyzlYlIml")
    private val houseTelvanniDeck = Deck.importCode("SPAKdKmxrpnNqtgByxeBjtliAEhNnXeeuCATxqnAybgsxlxioeqyxpqfqNkvmcxNsSqBlIrbeh")
    private val tribunalTempleDeck = Deck.importCode("SPAPjVaJqnbqqouGigfeyHlkaAwboNtDkCAJfWovqhsHdVhFnNkArPAOcxoeoMrCvKuAsmdrfBfPydmalYvD")
    private val singletonDeck = Deck.importCode("SPCXaJbqwbigutkPnFrPohlacdtDvUlxlkuCqovZgOkCdcwOeibnqpferfrtjWfyhLejuxqnereAkAaAjtuGqtdVkudtijmmaMbNpaiyiFlifBnbkrcUcOaPpewDtqpRkZkboMgDxcfmgTkojXbDlwehmdAAAA")

    @Test
    fun `red deck tests`() {
        val a = DeckAnalysis(redDeck)
        assertThat(a.commonCount).isEqualTo(6)
        assertThat(a.rareCount).isEqualTo(6)
        assertThat(a.epicCount).isEqualTo(6)
        assertThat(a.legendaryCount).isEqualTo(6)

        assertThat(a.soulgemCost).isEqualTo(10500)
        assertThat(a.prophecyCount).isEqualTo(1)
        assertThat(a.creatureCount).isEqualTo(12)
        assertThat(a.itemCount).isEqualTo(2)
        assertThat(a.actionCount).isEqualTo(8)
        assertThat(a.supportCount).isEqualTo(2)
        assertThat(a.totalCards).isEqualTo(24)
        assertThat(a.totalUnique).isEqualTo(12)
        assertThat(a.className).isEqualTo("Strength")
        assertThat(a.attributesCount).containsExactlyEntriesOf(mapOf("Strength" to 24))
    }

    @Test
    fun `aldmeri dominion deck test`() {
        val a = DeckAnalysis(aldmeriDominionDeck)
        assertThat(a.commonCount).isEqualTo(36)
        assertThat(a.rareCount).isEqualTo(39)
        assertThat(a.epicCount).isEqualTo(0)
        assertThat(a.legendaryCount).isEqualTo(0)

        assertThat(a.soulgemCost).isEqualTo(5700)
        assertThat(a.prophecyCount).isEqualTo(6)
        assertThat(a.creatureCount).isEqualTo(60)
        assertThat(a.itemCount).isEqualTo(4)
        assertThat(a.actionCount).isEqualTo(11)
        assertThat(a.supportCount).isEqualTo(0)
        assertThat(a.totalCards).isEqualTo(75)
        assertThat(a.totalUnique).isEqualTo(32)
        assertThat(a.className).isEqualTo("Aldmeri Dominion")
        assertThat(a.attributesCount).containsAllEntriesOf(mapOf("Intelligence" to 37, "Willpower" to 16, "Agility" to 27, "Neutral" to 1))
    }

    @Test
    fun `daggerfall covenant deck test`() {
        val a = DeckAnalysis(daggerfallCovenantDeck)
        assertThat(a.commonCount).isEqualTo(32)
        assertThat(a.rareCount).isEqualTo(13)
        assertThat(a.epicCount).isEqualTo(18)
        assertThat(a.legendaryCount).isEqualTo(17)

        assertThat(a.soulgemCost).isEqualTo(30500)
        assertThat(a.prophecyCount).isEqualTo(7)
        assertThat(a.creatureCount).isEqualTo(52)
        assertThat(a.itemCount).isEqualTo(1)
        assertThat(a.actionCount).isEqualTo(26)
        assertThat(a.supportCount).isEqualTo(1)
        assertThat(a.totalCards).isEqualTo(80)
        assertThat(a.totalUnique).isEqualTo(51)
        assertThat(a.className).isEqualTo("Daggerfall Covenant")
        assertThat(a.attributesCount).containsAllEntriesOf(mapOf("Intelligence" to 49, "Strength" to 17, "Endurance" to 18, "Neutral" to 6))
    }

    @Test
    fun `DeckClass tests`() {
        val cRed = TestCard(name = "red", attributes = listOf("Strength")).createCard()
        val cBlue = TestCard(name = "blue", attributes = listOf("Intelligence")).createCard()
        val cYellow = TestCard(name = "yellow", attributes = listOf("Willpower")).createCard()
        val cPurple = TestCard(name = "purple", attributes = listOf("Endurance")).createCard()
        val cGreen = TestCard(name = "green", attributes = listOf("Agility")).createCard()
        val cGray = TestCard(name = "gray", attributes = listOf("Neutral")).createCard()

        assertThat(DeckAnalysis(Deck(cards = listOf(cRed))).deckClass).isEqualTo(DeckClass.STRENGTH)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue))).deckClass).isEqualTo(DeckClass.INTELLIGENCE)
        assertThat(DeckAnalysis(Deck(cards = listOf(cYellow))).deckClass).isEqualTo(DeckClass.WILLPOWER)
        assertThat(DeckAnalysis(Deck(cards = listOf(cPurple))).deckClass).isEqualTo(DeckClass.ENDURANCE)
        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen))).deckClass).isEqualTo(DeckClass.AGILITY)
        assertThat(DeckAnalysis(Deck(cards = listOf(cGray))).deckClass).isEqualTo(DeckClass.NEUTRAL)

        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen, cRed))).deckClass).isEqualTo(DeckClass.ARCHER)
        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen, cBlue))).deckClass).isEqualTo(DeckClass.ASSASSIN)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cRed))).deckClass).isEqualTo(DeckClass.BATTLEMAGE)
        assertThat(DeckAnalysis(Deck(cards = listOf(cRed, cYellow))).deckClass).isEqualTo(DeckClass.CRUSADER)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cYellow))).deckClass).isEqualTo(DeckClass.MAGE)
        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen, cYellow))).deckClass).isEqualTo(DeckClass.MONK)
        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen, cPurple))).deckClass).isEqualTo(DeckClass.SCOUT)
        assertThat(DeckAnalysis(Deck(cards = listOf(cPurple, cBlue))).deckClass).isEqualTo(DeckClass.SORCERER)
        assertThat(DeckAnalysis(Deck(cards = listOf(cPurple, cYellow))).deckClass).isEqualTo(DeckClass.SPELLSWORD)
        assertThat(DeckAnalysis(Deck(cards = listOf(cPurple, cRed))).deckClass).isEqualTo(DeckClass.WARRIOR)

        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen, cBlue, cRed))).deckClass).isEqualTo(DeckClass.HOUSE_DAGOTH)
        assertThat(DeckAnalysis(Deck(cards = listOf(cRed, cYellow, cGreen))).deckClass).isEqualTo(DeckClass.HOUSE_HLAALU)
        assertThat(DeckAnalysis(Deck(cards = listOf(cRed, cYellow, cPurple))).deckClass).isEqualTo(DeckClass.HOUSE_REDORAN)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cGreen, cPurple))).deckClass).isEqualTo(DeckClass.HOUSE_TELVANNI)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cYellow, cPurple))).deckClass).isEqualTo(DeckClass.TRIBUNAL_TEMPLE)

        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cYellow, cGreen))).deckClass).isEqualTo(DeckClass.ALDMERI_DOMINION)
        assertThat(DeckAnalysis(Deck(cards = listOf(cPurple, cRed, cBlue))).deckClass).isEqualTo(DeckClass.DAGGERFALL_COVENANT)
        assertThat(DeckAnalysis(Deck(cards = listOf(cGreen, cPurple, cRed))).deckClass).isEqualTo(DeckClass.EBONHEART_PACT)
        assertThat(DeckAnalysis(Deck(cards = listOf(cYellow, cGreen, cPurple))).deckClass).isEqualTo(DeckClass.EMPIRE_OF_CYRODIIL)
        assertThat(DeckAnalysis(Deck(cards = listOf(cRed, cBlue, cYellow))).deckClass).isEqualTo(DeckClass.GUILDSWORN)

        // different order, with neutral
        assertThat(DeckAnalysis(Deck(cards = listOf(cRed, cGreen, cGray))).deckClass).isEqualTo(DeckClass.ARCHER)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cRed, cGray, cYellow))).deckClass).isEqualTo(DeckClass.GUILDSWORN)

        // multiple copies
        assertThat(DeckAnalysis(Deck(cards = listOf(cRed, cGreen, cGray, cRed))).deckClass).isEqualTo(DeckClass.ARCHER)
        assertThat(DeckAnalysis(Deck(cards = listOf(cBlue, cRed, cBlue, cGray, cYellow))).deckClass).isEqualTo(DeckClass.GUILDSWORN)
    }

}