package tesl.rest.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.TestCard
import tesl.model.Deck
import tesl.rest.DeckClass

class DeckAnalysisTest {
    private val redDeck = Deck.importCode("SPAEcCiPhypuAEsGdzkawiAElDdDmgmV")
    private val redDeckAnalysis = DeckAnalysis(redDeck)

    @Test
    fun `rarity count test`() {
        assertThat(redDeckAnalysis.commonCount).isEqualTo(6)
        assertThat(redDeckAnalysis.rareCount).isEqualTo(6)
        assertThat(redDeckAnalysis.epicCount).isEqualTo(6)
        assertThat(redDeckAnalysis.legendaryCount).isEqualTo(6)
    }

    @Test
    fun `soulgem count`() {
        assertThat(redDeckAnalysis.soulgemCost).isEqualTo(10500)
    }

    @Test
    fun `prophecy count`() {
        assertThat(redDeckAnalysis.prophecyCount).isEqualTo(1)
    }

    @Test
    fun `creature count`() {
        assertThat(redDeckAnalysis.creatureCount).isEqualTo(12)
    }

    @Test
    fun `item count`() {
        assertThat(redDeckAnalysis.itemCount).isEqualTo(2)
    }

    @Test
    fun `action count`() {
        assertThat(redDeckAnalysis.actionCount).isEqualTo(8)
    }

    @Test
    fun `support count`() {
        assertThat(redDeckAnalysis.supportCount).isEqualTo(2)
    }

    @Test
    fun `class test`() {
        assertThat(redDeckAnalysis.className).isEqualTo("Strength")
    }

    @Test
    fun `deck class tests`() {
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