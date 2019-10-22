package tesl.rest.rx

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import tesl.rest.exceptions.ApiException

fun <T> asSingle(callable: () -> T): Single<T> {
    return Single.fromCallable(callable)
            .onErrorResumeNext { t: Throwable ->
                if (t is ApiException) Single.error(t) else Single.error(ApiException(t))
            }
            .subscribeOn(Schedulers.io())
}

fun <T> asMaybe(callable: () -> T): Maybe<T> {
    return Maybe.fromCallable(callable)
            .onErrorResumeNext { t: Throwable ->
                if (t is ApiException) Maybe.error(t) else Maybe.error(ApiException(t))
            }
            .subscribeOn(Schedulers.io())
}
