package tesl.rest.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import io.reactivex.Single
import mu.KotlinLogging
import tesl.rest.model.DeckInfo
import tesl.rest.reader.DeckInfoCreator
import tesl.rest.reader.ImageCreator
import tesl.rest.rx.asSingle
import javax.annotation.security.PermitAll
import javax.inject.Named

private val logger = KotlinLogging.logger {}

@Validated
@PermitAll
@Controller("/")
class TESLController(
    val deckInfoCreator: DeckInfoCreator,
    @Named("deckImageCreator") val deckImageCreator: ImageCreator
) {
    @Get("/info/{code}")
    fun info(@PathVariable code: String): Single<DeckInfo> {
        logger.info { "info for code $code" }
        return asSingle {
            deckInfoCreator.parse(code)
        }
    }

    @Get(value = "/image/{code}", produces = [MediaType.IMAGE_PNG])
    fun renderImage(@PathVariable code: String): Single<ByteArray> {
        logger.info { "deck image for code $code" }
        return asSingle {
            deckImageCreator.createImage(code)
        }
    }
}