package tesl.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject

@MicronautTest
class RestAPITest {
    @Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @Test
    fun `image should return png for a deck request`() {
        // When
        val response = client.toBlocking().retrieve("/image/SPABdqAAAA") // 1 x Close Call

        // Then
        assertThat(response.take(6)).endsWith("PNG\r\n")
    }

    @Test
    fun `info should fail with Bad Request when an invalid deck is specified for`() {
        //When
        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().retrieve("/info/bad-code")
        }

        //Then
        assertThat(exception.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(exception.message).isEqualTo("Invalid deck code")
    }

    @Test
    fun `image should fail with Bad Request when an invalid deck is specified for`() {
        //When
        val exception = assertThrows<HttpClientResponseException> {
            client.toBlocking().retrieve("/image/bad-code")
        }

        //Then
        assertThat(exception.status).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(exception.message).isEqualTo("Invalid deck code")
    }

    @Test
    fun `info should return correct shape for a deck request`() {
        // When
        val response = client.toBlocking().retrieve("/info/SPABdqAAAA") // 1 x Close Call

        // Then
        assertThat(prettify(response)).isEqualToIgnoringWhitespace(
            """
        |{
        |  "code": "SPABdqAAAA",
        |  "className": "Neutral",
        |  "commonCount": 0,
        |  "rareCount": 1,
        |  "epicCount": 0,
        |  "legendaryCount": 0,
        |  "soulgemCost": 100,
        |  "creatureCount": 0,
        |  "actionCount": 1,
        |  "itemCount": 0,
        |  "supportCount": 0,
        |  "cards": [
        |    {
        |      "count": 1,
        |      "card": {
        |        "id": "dff7b6f0-9b7c-579d-9944-a69045ce24a1",
        |        "name": "Close Call",
        |        "rarity": "Rare",
        |        "mana": 0,
        |        "power": -1,
        |        "health": -1,
        |        "type": "Action",
        |        "attributes": [
        |          "Neutral"
        |        ]
        |      }
        |    }
        |  ]
        |}
        |""".trimMargin()
        )

    }

    private val objectMapper = ObjectMapper()
    private fun prettify(response: String): String {
        val asObject = objectMapper.readValue<Any>(response)
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(asObject)
    }

}