package tesl.rest.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import io.reactivex.Maybe
import mu.KotlinLogging
import tesl.rest.model.DeckInfo
import tesl.rest.reader.DeckInfoCreator
import tesl.rest.reader.ImageCreator
import tesl.rest.rx.asMaybe
import javax.annotation.security.PermitAll

private val logger = KotlinLogging.logger {}

@Validated
@PermitAll
@Controller("/")
class TESLController(
    val deckInfoCreator: DeckInfoCreator,
    val imageCreator: ImageCreator
) {
    @Get("/info/{code}")
    fun info(@PathVariable code: String): Maybe<DeckInfo?> {
        logger.info { "info for code $code" }
        return asMaybe {
            deckInfoCreator.parse(code)
        }
    }

    @Get(value = "/image/{code}", produces = [MediaType.IMAGE_PNG])
    fun image(@PathVariable code: String): Maybe<ByteArray?> {
        logger.info { "image for code $code" }
        return asMaybe {
            imageCreator.createDeckImage(code)
        }
    }
}