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
        assertThat(IdGenerator.generateCardUUID("${TESLCard.sanitize("Ayleid Guardian")}-yW")).isEqualTo("b11bfece-ecd5-5e9f-bb50-df0e5bbcc9e0")
    }

    @Test
    fun `tamriel collection ids`() {
        val tamrielCards = listOf(
            Triple("Aldmeri Spellwright", "Bm", "932f24da-f030-54bc-9bea-b20e5f574711"),
            Triple("Altar of Spellmaking", "AY", "cbdacd40-8d43-55cb-9247-9085aabd46a3"),
            Triple("A New Era", "Be", "6c7044b1-9c5b-5e39-b281-0f20c310b175"),
            Triple("Call Dragon", "Bj", "8b892e6b-e55c-542e-bab2-55d1dac631b6"),
            Triple("Death Scythe", "Bk", "4701e6a2-addf-5fc6-88e0-d0f157a8399c"),
            Triple("Enchanted Ring", "Bc", "834ac9df-6540-59ca-8ef2-c63011d9df68"),
            Triple("Explore", "Bf", "728d4a26-de0b-58de-8984-de830fa50433"),
            Triple("Feed", "Bi", "2d385407-6eec-52c7-b5a5-0ed7ee492cce"),
            Triple("Midnight Burial", "Bn", "6b7b2b1f-f201-53bf-bb0b-8dd93e05a0f7"),
            Triple("Obstinate Goat", "Bg", "25f71646-1ec4-55ef-a0a0-bd297ca25e25"),
            Triple("Ordinator of the Almsivi", "Bb", "4d748dac-33f4-5c43-8349-5e0ff9f873d0"),
            Triple("Pact Outcast", "AX", "59da643d-9113-5099-854e-64911e02fc0b"),
            Triple("Red Mountain", "Ba", "ffc4490e-db4c-504e-99f1-396526606a7a"),
            Triple("Smuggler Underboss", "Bo", "350db23e-9b84-551b-ba8c-eaba00a5f37d"),
            Triple("Strange Brew", "Bl", "33055c68-1c2d-517f-860b-ea8cc343b377"),
            Triple("Wild Echatere", "Bh", "1c66993e-a9c3-594a-9449-15bc3b1d5bc3")
        )

        tamrielCards.forEach { (name, code, id) ->
            println("${IdGenerator.generateCardUUID("${name}-${code}")} : $name")
            assertThat(IdGenerator.generateCardUUID("${name}-${code}")).isEqualTo(id)
        }

    }
}