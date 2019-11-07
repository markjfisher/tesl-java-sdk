package tesl.rest.reader

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tesl.rest.exceptions.BadRequestException

class DeckInfoCreatorTest {

    @Test
    fun `an invalid code throws an exception`() {
        val creator = DeckInfoCreator()
        val e = assertThrows<BadRequestException> {
            creator.parse("X")
        }

        assertThat(e.message).isEqualTo("Invalid deck code")
    }

}