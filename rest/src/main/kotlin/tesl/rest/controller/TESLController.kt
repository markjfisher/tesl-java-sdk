package tesl.rest.controller

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import io.reactivex.Maybe
import tesl.model.DeckInfo
import tesl.rest.reader.DeckReader
import tesl.rest.rx.asMaybe
import javax.annotation.security.PermitAll

@Validated
@PermitAll
@Controller("/tesl")
class TESLController(
    val deckReader: DeckReader
) {
    @Get("/info/{code}")
    fun info(@PathVariable code: String): Maybe<DeckInfo?> {
        return asMaybe {
            deckReader.parse(code)
        }
    }
}