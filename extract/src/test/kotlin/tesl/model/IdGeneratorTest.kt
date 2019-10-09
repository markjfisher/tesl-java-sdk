package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.idgenerator.IdGenerator

class IdGeneratorTest {
    @Test
    fun `id generator makes uuids from name and code`() {
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Brotherhood Suspect")}-xy")).isEqualTo("03b6646b-bc20-58b7-a726-aa3dcfa3c14d")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Bedlam")}-xx")).isEqualTo("901d37ea-448d-51b2-bac1-827b7efa97d8")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Whodunit?")}-xR")).isEqualTo("04944253-72c8-5e75-8096-456dafdbed7f")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Hannibal Traven")}-xD")).isEqualTo("2aca89da-7fac-5d35-beee-85c582ac26b2")
    }
}