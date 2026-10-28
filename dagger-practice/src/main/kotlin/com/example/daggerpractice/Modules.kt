package com.example.daggerpractice

import dagger.Binds
import dagger.Module
import dagger.Provides
import java.time.Clock
import javax.inject.Singleton

/**
 * Plain Dagger modules: no @InstallIn. Hilt decides where a module goes with @InstallIn; here each
 * component lists its modules by hand (see [PracticeComponent]).
 */
@Module
object ClockModule {
    // Not our class and built by a factory method: it needs @Provides, like OkHttp or Room in the app.
    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemUTC()
}

@Module
abstract class ApiModule {
    // Binds the interface to its implementation, like @Binds would in a Hilt module.
    @Binds
    @Singleton
    abstract fun bindWorkoutApi(impl: FakeWorkoutApi): WorkoutApi
}
