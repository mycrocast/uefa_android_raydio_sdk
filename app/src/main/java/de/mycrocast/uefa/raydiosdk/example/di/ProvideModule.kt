package de.mycrocast.uefa.raydiosdk.example.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.mycrocast.android.play_by_ear.sdk.connection.domain.PlayByEarConnection
import de.mycrocast.android.play_by_ear.sdk.core.data.PlayByEarSDKBuilder
import de.mycrocast.android.play_by_ear.sdk.core.domain.PlayByEarSDK
import de.mycrocast.android.play_by_ear.sdk.core.domain.PlayByEarSDKCredentials
import de.mycrocast.android.play_by_ear.sdk.livestream.loader.domain.PlayByEarLivestreamLoader
import de.mycrocast.android.play_by_ear.sdk.livestream.player.domain.PlayByEarLivestreamPlayer
import de.mycrocast.android.play_by_ear.sdk.logger.PlayByEarLogger
import de.mycrocast.raydio.uefa.sdk.livestream.container.domain.RaydioLivestreamGroupContainer
import de.mycrocast.raydio.uefa.sdk.livestream.loader.domain.RaydioLivestreamLoader
import de.mycrocast.raydio.uefa.sdk.livestream.player.domain.RaydioLivestreamPlayer
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.data.MainPlayStateContainer
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayStateContainer
import javax.inject.Singleton

/**
 * Module which includes all dependencies the example application need to inject in viewmodels or services.
 */
@Module
@InstallIn(SingletonComponent::class)
class ProvideModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "de.mycrocast.raydio.uefa.sdk.preferences",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideSDKCredentials(): PlayByEarSDKCredentials {
        return object : PlayByEarSDKCredentials {
            override val token: String = ""
        }
    }

    @Provides
    @Singleton
    fun provideSDKLogger(): PlayByEarLogger {
        return object : PlayByEarLogger {
            override fun info(tag: String, message: String) {
                Log.i(tag, message)
            }

            override fun warning(tag: String, message: String) {
                Log.w(tag, message)
            }

            override fun error(tag: String, message: String, throwable: Throwable) {
                Log.e(tag, message + ": ${throwable.message}")
            }
        }
    }

    @Provides
    @Singleton
    fun provideSDK(
        preferences: SharedPreferences,
        credentials: PlayByEarSDKCredentials,
        logger: PlayByEarLogger
    ): PlayByEarSDK {
        return PlayByEarSDKBuilder(credentials, preferences, logger).build()
    }

    @Provides
    @Singleton
    fun provideConnection(
        sdk: PlayByEarSDK
    ): PlayByEarConnection {
        return sdk.connection
    }

    @Provides
    @Singleton
    fun provideLivestreamLoader(
        sdk: PlayByEarSDK
    ): PlayByEarLivestreamLoader {
        return sdk.livestreamLoader
    }

    @Provides
    @Singleton
    fun provideLivestreamContainer(
        sdk: PlayByEarSDK
    ): RaydioLivestreamGroupContainer {
        return sdk.livestreamGroupContainer
    }

    @Provides
    @Singleton
    fun provideLivestreamPlayerFactory(
        sdk: PlayByEarSDK
    ): PlayByEarLivestreamPlayer.Factory {
        return sdk.livestreamPlayerFactory
    }

    @Provides
    @Singleton
    fun providePlayStateContainer(): PlayStateContainer {
        return MainPlayStateContainer()
    }
}