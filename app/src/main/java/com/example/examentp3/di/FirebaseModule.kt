package com.example.examentp3.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Null when the build has no google-services.json: the google-services plugin is not applied,
     * FirebaseInitProvider creates no FirebaseApp and FirebaseAuth.getInstance() would throw.
     * The app then keeps the demo access (SPEC p.1, EXT-01).
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth? =
        if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseAuth.getInstance()
}
