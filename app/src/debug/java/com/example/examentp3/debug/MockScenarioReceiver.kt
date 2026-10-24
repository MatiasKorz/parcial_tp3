package com.example.examentp3.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.examentp3.data.remote.interceptor.MockScenario
import com.example.examentp3.data.remote.interceptor.MockScenarioStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

private const val TAG = "MockScenario"
private val SCENARIOS = setOf("normal", "empty", "error", "malformed")

/**
 * Debug-only switch for the mock scenarios of RF05, driven from a terminal (README):
 * adb shell am broadcast -n com.example.examentp3/.debug.MockScenarioReceiver --es scenario error --ei delay 3000
 * Calling it without extras goes back to normal.
 */
class MockScenarioReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MockScenarioEntryPoint {
        fun mockScenarioStore(): MockScenarioStore
    }

    override fun onReceive(context: Context, intent: Intent) {
        val scenario = intent.getStringExtra("scenario")?.trim()
        if (scenario != null && scenario !in SCENARIOS) {
            // The mock answers 400 to unknown values (case-sensitive), so they are rejected here.
            Log.w(TAG, "Unknown scenario '$scenario'. Use one of $SCENARIOS.")
            return
        }
        val delayMs = intent.getIntExtra("delay", 0).coerceIn(0, 10_000)
        val store = EntryPointAccessors
            .fromApplication(context.applicationContext, MockScenarioEntryPoint::class.java)
            .mockScenarioStore()
        store.set(
            MockScenario(
                scenario = scenario?.takeIf { it != "normal" },
                delayMs = delayMs.takeIf { it > 0 },
            ),
        )
        Log.i(TAG, "Mock scenario: ${scenario ?: "normal"}, delay: $delayMs ms")
    }
}
