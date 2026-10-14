
package com.example.examentp3.data.remote.interceptor

import okhttp3.logging.HttpLoggingInterceptor

fun createLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
