package com.example.examentp3.viewmodel

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class UiErrorTest {

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun http503_isServer() {
        assertEquals(UiError.Server, httpError(503).toUiError())
    }

    @Test
    fun http404_isNotFound() {
        assertEquals(UiError.NotFound, httpError(404).toUiError())
    }

    @Test
    fun malformedJson_isInvalidResponse_notNoConnection() {
        assertEquals(UiError.InvalidResponse, MalformedJsonException("INVALID_JSON").toUiError())
    }

    @Test
    fun gsonSyntaxError_isInvalidResponse() {
        assertEquals(UiError.InvalidResponse, JsonSyntaxException(MalformedJsonException("INVALID_JSON")).toUiError())
    }

    @Test
    fun truncatedBody_isInvalidResponse() {
        assertEquals(UiError.InvalidResponse, EOFException().toUiError())
    }

    @Test
    fun timeout_isTimeout() {
        assertEquals(UiError.Timeout, SocketTimeoutException().toUiError())
    }

    @Test
    fun serverStopped_isNoConnection() {
        assertEquals(UiError.NoConnection, ConnectException("Connection refused").toUiError())
    }

    @Test
    fun otherIoError_isNoConnection() {
        assertEquals(UiError.NoConnection, IOException().toUiError())
    }

    @Test
    fun unexpectedError_isUnknown() {
        assertEquals(UiError.Unknown, IllegalStateException().toUiError())
    }

    @Test(expected = CancellationException::class)
    fun cancellation_isRethrown() {
        CancellationException().toUiError()
    }
}
