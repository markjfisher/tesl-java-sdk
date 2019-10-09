package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TESLCardTest {
    @Test
    fun `sanitize replaces or removes names`() {
        assertThat(TESLCard.sanitize("this?'and'////////////that and   more")).isEqualTo("this_and_that_and_more")
    }

}