package com.example.examentp3.data.remote.interceptor

import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/** Mock headers for RF05: [scenario] is empty, error or malformed; null means normal. */
data class MockScenario(
    val scenario: String? = null,
    val delayMs: Int? = null,
)

/** Current scenario, changed at runtime from adb in debug builds (see the README). */
@Singleton
class MockScenarioStore @Inject constructor() {
    private val current = AtomicReference(MockScenario())

    fun get(): MockScenario = current.get()

    fun set(scenario: MockScenario) = current.set(scenario)
}

/** Adds X-Mock-Scenario and X-Mock-Delay-Ms to every request. Only installed in debug builds. */
class MockScenarioInterceptor @Inject constructor(
    private val store: MockScenarioStore,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val scenario = store.get()
        val request = chain.request().newBuilder().apply {
            scenario.scenario?.let { header("X-Mock-Scenario", it) }
            scenario.delayMs?.let { header("X-Mock-Delay-Ms", it.toString()) }
        }.build()
        return chain.proceed(request)
    }
}
