package tesl.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class DecoderTests {
    @Test
    fun `convert count marker to value for DECK`() {
        val decoder = Decoder(DecoderType.DECK)
        assertThat(decoder.decodeCountMarker("AA")).isEqualTo(0)
        assertThat(decoder.decodeCountMarker("AB")).isEqualTo(1)
        assertThat(decoder.decodeCountMarker("AZ")).isEqualTo(25)
        assertThat(decoder.decodeCountMarker("BA")).isEqualTo(26)
        assertThat(decoder.decodeCountMarker("BZ")).isEqualTo(51)
        assertThat(decoder.decodeCountMarker("ZZ")).isEqualTo(25 * 26 + 25)
    }

    @Test
    fun `convert count marker to value for COLLECTION`() {
        val decoder = Decoder(DecoderType.COLLECTION)
        assertThat(decoder.decodeCountMarker("!!")).isEqualTo(0)
        assertThat(decoder.decodeCountMarker("!\"")).isEqualTo(1)
        assertThat(decoder.decodeCountMarker("!z")).isEqualTo(89)
        assertThat(decoder.decodeCountMarker("\"!")).isEqualTo(90)
        assertThat(decoder.decodeCountMarker("zz")).isEqualTo(89 * 90 + 89)
    }

    @Test
    fun `checking valid code strings for DECK`() {
        // Not right format
        val decoder = Decoder(DecoderType.DECK)

        assertThat(decoder.isCodeValid("NOTSP")).isFalse()
        assertThat(decoder.isCodeValid("SP34567")).isFalse()

        // Not enough cards for given marker lengths
        assertThat(decoder.isCodeValid("SPABABAB")).isFalse()
        assertThat(decoder.isCodeValid("SPAAABAB")).isFalse()
        assertThat(decoder.isCodeValid("SPAAAAAB")).isFalse()
        assertThat(decoder.isCodeValid("SPACxxAAAA")).isFalse()
        assertThat(decoder.isCodeValid("SPAAACxxAA")).isFalse()
        assertThat(decoder.isCodeValid("SPAAAAACxx")).isFalse()

        // Valid codes
        assertThat(decoder.isCodeValid("SPAAAAAA")).isTrue()
        assertThat(decoder.isCodeValid("SPABxxAAAA")).isTrue()
        assertThat(decoder.isCodeValid("SPAAABxxAA")).isTrue()
        assertThat(decoder.isCodeValid("SPAAAAABxx")).isTrue()
        assertThat(decoder.isCodeValid("SPACxxyyAAAA")).isTrue()
        assertThat(decoder.isCodeValid("SPAAACxxyyAA")).isTrue()
        assertThat(decoder.isCodeValid("SPAAAAACxxyy")).isTrue()

        assertThat(decoder.isCodeValid("SPABxxAByyAA")).isTrue()
        assertThat(decoder.isCodeValid("SPACxxyyABzzAA")).isTrue()
        assertThat(decoder.isCodeValid("SPADaabbccACddeeABff")).isTrue()
    }

    @Test
    fun `checking valid code strings for COLLECTION`() {
        // Not right format
        val decoder = Decoder(DecoderType.COLLECTION)

        assertThat(decoder.isCodeValid("NOTSP")).isFalse()
        assertThat(decoder.isCodeValid("SP34567")).isFalse()

        // Not enough cards for given marker lengths
        assertThat(decoder.isCodeValid("SP!\"!\"")).isFalse()

        // Valid codes
        assertThat(decoder.isCodeValid("SP!!!!!!")).isTrue()
        assertThat(decoder.isCodeValid("SP!\"xx!!!!")).isTrue()

        // An old version of my collection :)
        assertThat(decoder.isCodeValid("SP#\"bkiPlbgWkoftospSohmJjRrLoZsefZtBgckVcEjUoGmgiCutmlhmtPiHpIjbitaonRhrjDcrgGnOgFcfhlgRdbmRablRsAqImUpBrZlpoUbPdOrrkIaUiqkZiZoliAboaPbTflbjhhrHbRiOilnpnHeukPqvcTckeljjmzeNaJeypvqptDoKiQahnujPpMijbFcbjWktcRupokgtqdbNiXfTtwdQqqgAdrklaOkBkhlfddqJsgbGhPfqhOcKnvaHaWrFljkngOiJgwgieZsIrsrXlGgMdDkWhDhzanfdpctzlIjthMaiavskkqdanIpafIeYqYbunAamlcqnpOqefvogpllamXpLnemVnNnFqc\"8qkaNbfePsfoMqBqlagmkkjtCeDsvgxfEmdoorTjOdyqgjschkLmBdvoDeKnYlCisaklwaToPhUoimfkXfwfNezsRarlMkQjyqWlAfGcMoSjGoflxdYerngeQpFtOqGiGmmjJkRnDesbKtdoJugcCuAgvrRpRpDtGjeichBaMsmcwuskGjouDhVhSdkgqdZuEbCcicagddzsMqmbmjNdLtynrnZdfpCpVbM%YedgPgEtMtQmtpWrGazghoQbQfspiiLgugmechfksfSmEiBiNrBronVgyadbptgtxnleGtnrluFlZaukvevmjoymInMbyebsdqiaDqFacudpEphimsHmpsXeqnTmacFiwpZiDrybcaysLcJuzsymYaXtamSqOpHbgbAihnoglcGuhcSpstvpwrAcBgzdWgIdlqLmhrCiElhdBjMaVcyrvcZdKuluohucxkJbYtlnjquiagJtimGsasYmDlYiIsQoenXrcgaoOcnjknaoxcunBqTcIqPdpsWsJnLeJrMbBoteodIhsgQpQeegCoVrkhEbHhQhJuquvlyomiomPurmFhgsUiSaFjmpxlWemgsslnheXmCdsdgkcpzoqsVfRdofWhwrigThWfrfieRcUhbjHoEcgkrceldaRjwopdqnGppmLjEgUgphNujlDjitmhtclkEmqnPcXaIaKprcvjqbhgeiKtuternaCnsqUsPdCtSdhhjfadPdTfFntkitqnymwsFhIrubtdFmWdEfkuBdRkxlTcPhHjSmTeIhvnzhXnftHlLsKkysEcpikdefBtjtorbenbrgnfYrpmQoIfAtFdnltnckNiuberedmnicQeVtAtstReSgojruntctEcodUiiqNrElOnClEjTpysGsOeHlrlXpUrPkkrKqwfJkDrQholJlVqQtknSjXlmjLbDfhqDjnsCumnKttoTnwjCcjqaixfUmNouswuekzdMhaqHocawfLmHnxqhndukpeeflzcOqCtrqXtIkbaQmZmcgjalcNqRkUsThyfpfVbitNkTobbshCsSlBmypmqysNtLbU")).isTrue()
    }

    @Test
    fun `checking import code for unknown codes and validity`() {
        val decoder = Decoder(DecoderType.DECK)

        var checkResult = decoder.checkImportCode("NOTSP")
        assertThat(checkResult.first).isFalse()
        assertThat(checkResult.second).isEmpty()

        checkResult = decoder.checkImportCode("SPAAAAAA")
        assertThat(checkResult.first).isTrue()
        assertThat(checkResult.second).isEmpty()

        checkResult = decoder.checkImportCode("SPAEaaabzazbACaczcACzdad")
        assertThat(checkResult.first).isTrue()
        assertThat(checkResult.second).containsExactlyInAnyOrder("za", "zb", "zc", "zd")
    }

    @Test
    @Disabled("Don't need this yet. Also requires the decoder to know about cards directly. Might be better under Deck/Collection")
    fun `unique cards are only seen once`() {
        val decoder = Decoder(DecoderType.DECK)

        assertThat(decoder.isCodeValid("SPAAABxDAA")).isFalse()
        assertThat(decoder.isCodeValid("SPAAAAABxD")).isFalse()

    }
}