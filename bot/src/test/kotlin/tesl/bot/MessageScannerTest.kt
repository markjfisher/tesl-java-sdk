package tesl.bot

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class MessageScannerTest {
    @Test
    fun `scanning text for messages`() {
        assertThat(MessageScanner.scanMessage("this !{{ contains }} messages to !{{ scan here }}")).containsExactly("contains", "scan here")
        assertThat(MessageScanner.scanMessage("multi-line !{{ contains\n carriage returns }}")).containsExactly("contains\n carriage returns")
        assertThat(MessageScanner.scanMessage("this contains no matches")).isEmpty()
        assertThat(MessageScanner.scanMessage("this contains !{{ !{{ double embed for some reason }} }}")).containsExactly("!{{ double embed for some reason")
    }

}