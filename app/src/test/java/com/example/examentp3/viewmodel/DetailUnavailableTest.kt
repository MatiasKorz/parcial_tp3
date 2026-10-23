package com.example.examentp3.viewmodel

import java.io.IOException
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class DetailUnavailableTest {

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "".toResponseBody()))

    @Test
    fun http404_isNotFound() {
        assertEquals(DetailUnavailable.NotFound, httpError(404).toDetailUnavailable())
    }

    @Test
    fun noConnection_isNotAvailableOffline() {
        assertEquals(DetailUnavailable.NotAvailableOffline, IOException("Connection refused").toDetailUnavailable())
    }

    @Test
    fun otherHttpError_isNotAvailableOffline() {
        assertEquals(DetailUnavailable.NotAvailableOffline, httpError(503).toDetailUnavailable())
    }
}
