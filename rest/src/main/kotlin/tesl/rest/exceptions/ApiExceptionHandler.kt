package tesl.rest.exceptions

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpResponseProvider
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import mu.KotlinLogging
import javax.inject.Singleton

private val logger = KotlinLogging.logger {}
private const val genericErrorMessage = "Unexpected error occurred"

open class ApiException : Exception, HttpResponseProvider {
    private val response: HttpResponse<Any>

    override fun getResponse(): HttpResponse<Any> = response

    constructor(cause: Throwable) : super(cause) {
        response = HttpResponse.serverError(mapOf("message" to genericErrorMessage))
    }

    constructor(reason: String, respondWith: HttpStatus) : super(reason) {
        val messageForClient = if (respondWith == HttpStatus.INTERNAL_SERVER_ERROR) genericErrorMessage else reason
        response = HttpResponseFactory.INSTANCE
                .status<Any>(respondWith)
                .body(mapOf("message" to messageForClient))
    }
}

/**
 * Micronaut chooses an ExceptionHandler bean based on Type argument (see RoutingInBoundHandler line 312),
 * therefore wrap all exceptions with ApiException to ensure they are handled by this handler.
 */
@Produces
@Singleton
@Requires(classes = [Exception::class, ExceptionHandler::class])
class ApiExceptionHandler() : ExceptionHandler<ApiException, HttpResponse<Any>> {

    override fun handle(request: HttpRequest<Any>, exception: ApiException): HttpResponse<Any> {
        if (exception.response.status == HttpStatus.INTERNAL_SERVER_ERROR) {
            logger.error(exception) { "Unexpected error for request $request" }
        }

        return exception.response
    }
}

class BadRequestException(message: String) : ApiException(message, HttpStatus.BAD_REQUEST)
class ServiceException(message: String) : ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR)
class FakeException : ApiException("Fault injected for testing", HttpStatus.INTERNAL_SERVER_ERROR)