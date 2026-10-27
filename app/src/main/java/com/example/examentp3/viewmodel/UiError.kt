package com.example.examentp3.viewmodel

import androidx.annotation.StringRes
import com.example.examentp3.R
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

/** Failure of a remote query, shown with its own message and a retry action (RF05). */
sealed interface UiError {
    @get:StringRes val message: Int

    data object NoConnection : UiError { override val message = R.string.error_no_connection }
    data object Timeout : UiError { override val message = R.string.error_timeout }
    data object NotFound : UiError { override val message = R.string.error_not_found }
    data object Server : UiError { override val message = R.string.error_server }
    data object InvalidResponse : UiError { override val message = R.string.error_invalid_response }
    data object Unknown : UiError { override val message = R.string.error_unknown }
}

/**
 * Malformed JSON arrives as an IOException subclass, so it is matched before IOException;
 * otherwise X-Mock-Scenario: malformed (HTTP 200) would read as "no connection".
 * A 404 is kept apart from other HTTP errors: the mock answers it for a detail that does not exist.
 */
fun Throwable.toUiError(): UiError = when (this) {
    is CancellationException -> throw this
    is HttpException -> if (code() == 404) UiError.NotFound else UiError.Server
    is MalformedJsonException, is EOFException, is JsonParseException -> UiError.InvalidResponse
    is SocketTimeoutException -> UiError.Timeout
    is IOException -> UiError.NoConnection
    else -> UiError.Unknown
}
