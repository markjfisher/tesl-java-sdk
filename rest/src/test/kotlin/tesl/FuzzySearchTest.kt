package tesl

import me.xdrop.fuzzywuzzy.FuzzySearch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.model.CardCache

class FuzzySearchTest {
    @Test
    fun `extract with limits and cutoffs only returns correct amount`() {
        val top3 = FuzzySearch.extractTop("guard", CardCache.all(), { it.name }, 3)
        assertThat(top3).hasSize(3)

        val cutoff72Top3 = FuzzySearch.extractTop("guard", CardCache.all(), { it.name }, 3, 72)
        assertThat(cutoff72Top3).hasSize(3)

        val cutoff100Top3 = FuzzySearch.extractTop("guard", CardCache.all(), { it.name }, 3, 100)
        assertThat(cutoff100Top3).hasSize(0)

        val withAlts = FuzzySearch.extractTop("arrw stor", CardCache.all(), { it.name }, 3, 72)
        assertThat(withAlts).hasSize(2)

        val noAlts = FuzzySearch.extractTop("arrw stor", CardCache.all().filter { !it.isAlt }, { it.name }, 3, 72)
        assertThat(noAlts).hasSize(1)
    }
}