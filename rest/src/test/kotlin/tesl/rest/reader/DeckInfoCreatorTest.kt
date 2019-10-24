package tesl.rest.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeckInfoCreatorTest {

    @Test
    fun `an invalid code returns no deck info`() {
        // This might change, but for now, we have null represent non-valid deck
        val creator = DeckInfoCreator()
        assertThat(creator.parse("X")).isNull()
    }

}