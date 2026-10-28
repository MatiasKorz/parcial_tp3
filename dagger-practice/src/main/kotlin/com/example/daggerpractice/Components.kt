package com.example.daggerpractice

import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import javax.inject.Inject
import javax.inject.Scope
import javax.inject.Singleton

/**
 * The root of the graph, written by hand. Hilt predefines it as SingletonComponent and creates it in
 * the @HiltAndroidApp Application; here [main] creates it with the generated DaggerPracticeComponent.
 */
@Singleton
@Component(modules = [ClockModule::class, ApiModule::class])
interface PracticeComponent {

    fun workoutRepository(): WorkoutRepository

    /** Exposed to show @Binds: asking for the interface returns [FakeWorkoutApi]. */
    fun workoutApi(): WorkoutApi

    /** Child components are created from their parent, as Hilt does for ViewModelComponent. */
    fun screenComponentFactory(): ScreenComponent.Factory

    @Component.Factory
    interface Factory {
        // A value known only at runtime enters the graph here; Hilt does the same with @ApplicationContext.
        fun create(@BindsInstance @BaseUrl baseUrl: String): PracticeComponent
    }
}

/** A custom scope: one instance per [ScreenComponent]. Hilt predefines @ViewModelScoped for this. */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenScope

/**
 * Lives as long as one screen, like Hilt's ViewModelComponent. The workout id plays the role of the
 * SavedStateHandle argument that Hilt hands to each ViewModel.
 */
@ScreenScope
@Subcomponent
interface ScreenComponent {

    fun presenter(): WorkoutPresenter

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance workoutId: String): ScreenComponent
    }
}

/** Stands in for a @HiltViewModel: one per screen, with the singleton repository injected. */
@ScreenScope
class WorkoutPresenter @Inject constructor(
    val repository: WorkoutRepository,
    private val workoutId: String,
) {
    fun title(): String = repository.find(workoutId)?.name ?: "Workout $workoutId not found"
}
