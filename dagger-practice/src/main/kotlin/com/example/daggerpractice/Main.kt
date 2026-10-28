package com.example.daggerpractice

/**
 * Runs the graph and prints what Dagger built. In the app, Hilt does each of these steps without code:
 * it creates the root component, opens a child component per ViewModel and injects into it.
 */
fun main() {
    // 1. Build the root component by hand. DaggerPracticeComponent is generated at compile time.
    val app: PracticeComponent = DaggerPracticeComponent.factory().create(baseUrl = "http://10.0.2.2:8000/")

    // 2. @Singleton: the same repository every time it is requested from this component.
    val repository = app.workoutRepository()
    println("Repository is a singleton: ${repository === app.workoutRepository()}")
    println("Workouts: ${repository.workouts().joinToString { it.name }} (loaded at ${repository.loadedAt()})")

    // 3. One child component per screen, as Hilt does per ViewModel.
    val details = app.screenComponentFactory().create(workoutId = "w02")
    val other = app.screenComponentFactory().create(workoutId = "w03")
    println("Screen w02: ${details.presenter().title()}")
    println("Screen w03: ${other.presenter().title()}")

    // 4. @ScreenScope: one presenter per screen component, different between screens.
    println("Same presenter inside one screen: ${details.presenter() === details.presenter()}")
    println("Different presenter between screens: ${details.presenter() !== other.presenter()}")

    // 5. Both screens share the parent's singleton repository.
    println("Screens share the repository: ${details.presenter().repository === other.presenter().repository}")
}
