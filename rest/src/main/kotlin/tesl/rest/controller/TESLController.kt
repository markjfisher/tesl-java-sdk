package tesl.rest.controller

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import io.reactivex.Maybe
import tesl.rest.model.DeckInfo
import tesl.rest.reader.DeckInfoCreator
import tesl.rest.reader.ImageCreator
import tesl.rest.rx.asMaybe
import javax.annotation.security.PermitAll

@Validated
@PermitAll
@Controller("/tesl")
class TESLController(
    val deckInfoCreator: DeckInfoCreator,
    val imageCreator: ImageCreator
) {
    @Get("/deck/info/{code}")
    fun info(@PathVariable code: String): Maybe<DeckInfo?> {
        return asMaybe {
            deckInfoCreator.parse(code)
        }
    }

    @Get(value = "/deck/image/{code}", produces = [MediaType.IMAGE_PNG])
    fun image(@PathVariable code: String): Maybe<ByteArray?> {
        return asMaybe {
            imageCreator.createDeckImage(code)
        }
    }
}