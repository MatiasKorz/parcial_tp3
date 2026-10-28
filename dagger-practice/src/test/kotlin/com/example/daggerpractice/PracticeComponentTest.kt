package com.example.daggerpractice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test

/** Checks the scopes that Dagger enforces in the generated graph (EXT-07). */
class PracticeComponentTest {

    private val app = DaggerPracticeComponent.factory().create(baseUrl = "http://test/")

    @Test
    fun singleton_isTheSameInstanceOnEveryRequest() {
        assertSame(app.workoutRepository(), app.workoutRepository())
    }

    @Test
    fun binds_providesTheImplementationWithTheBaseUrlOfBindsInstance() {
        val api = app.workoutApi()
        assertEquals(FakeWorkoutApi::class, api::class)
        assertEquals("http://test/", api.baseUrl)
    }

    @Test
    fun screenScope_isOneInstancePerScreenComponent() {
        val screen = app.screenComponentFactory().create("w01")
        assertSame(screen.presenter(), screen.presenter())
    }

    @Test
    fun screenScope_differsBetweenScreensButSharesTheSingleton() {
        val first = app.screenComponentFactory().create("w01").presenter()
        val second = app.screenComponentFactory().create("w02").presenter()
        assertNotSame(first, second)
        assertSame(first.repository, second.repository)
    }

    @Test
    fun screenArgument_isInjectedIntoThePresenter() {
        assertEquals("Lower Body Strength", app.screenComponentFactory().create("w03").presenter().title())
    }
}
