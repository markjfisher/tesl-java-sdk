package tesl.oldmodel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tesl.idgenerator.IdGenerator
import tesl.oldmodel.TESLCard

class IdGeneratorTest {
    @Test
    fun `id generator makes uuids from name and code`() {
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Brotherhood Suspect")}-xy")).isEqualTo("03b6646b-bc20-58b7-a726-aa3dcfa3c14d")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Bedlam")}-xx")).isEqualTo("901d37ea-448d-51b2-bac1-827b7efa97d8")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Whodunit?")}-xR")).isEqualTo("04944253-72c8-5e75-8096-456dafdbed7f")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Hannibal Traven")}-xD")).isEqualTo("2aca89da-7fac-5d35-beee-85c582ac26b2")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Horse Armor")}-yV")).isEqualTo("0a605eef-60d2-5c0e-8c37-57c29581c0f0")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Champion of the Arena")}-yJ")).isEqualTo("8d0309c1-e74d-5d69-ae6e-cc5eb37302a5")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Xivkyn Channeler")}-xE")).isEqualTo("97d0aa50-6e8c-5c66-96bc-0b5b8b495d63")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Oblivion Gate")}-__")).isEqualTo("fded515c-54ea-51cc-95be-a11bd3827130")
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Rimmen Purveyor")}-yS")).isEqualTo("6ad7fb74-596c-590c-ab9f-9baf14493b52")
    }
}