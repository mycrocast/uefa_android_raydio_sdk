package de.mycrocast.uefa.raydiosdk.example.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.data.MainPlayStateContainer
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayStateContainer
import de.mycrocast.raydio.uefa.sdk.connection.domain.RaydioConnection
import de.mycrocast.raydio.uefa.sdk.core.data.RaydioSDKBuilder
import de.mycrocast.raydio.uefa.sdk.core.domain.RaydioSDK
import de.mycrocast.raydio.uefa.sdk.core.domain.RaydioSDKCredentials
import de.mycrocast.raydio.uefa.sdk.livestream.container.domain.RaydioLivestreamGroupContainer
import de.mycrocast.raydio.uefa.sdk.livestream.loader.domain.RaydioLivestreamLoader
import de.mycrocast.raydio.uefa.sdk.livestream.player.domain.RaydioLivestreamPlayer
import de.mycrocast.raydio.uefa.sdk.logger.RaydioInteraction
import de.mycrocast.raydio.uefa.sdk.logger.RaydioLogger
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
    fun provideSDKCredentials(): RaydioSDKCredentials {
        return object : RaydioSDKCredentials {
            override val clubId: Long = 235617L
        }
    }

    @Provides
    @Singleton
    fun provideRaydioLogger(): RaydioLogger {
        return object : RaydioLogger {
            override fun info(tag: String, message: String) {
                Log.i(tag, message)
            }

            override fun warning(tag: String, message: String) {
                Log.w(tag, message)
            }

            override fun error(tag: String, message: String, throwable: Throwable) {
                Log.e(tag, message + ": ${throwable.message}")
            }

            override fun interaction(interaction: RaydioInteraction) {
                when (interaction) {
                    is RaydioInteraction.StartPlayLivestream -> {
                        // TODO add your google analytics: livestream play was started

                        val userId = interaction.userId
                        val streamId = interaction.livestreamId
                        Log.i("RaydioInteraction", "User $userId starts playing livestream $streamId")
                    }

                    is RaydioInteraction.StopPlayLivestream -> {
                        // TODO add your google analytics: livestream play was stopped

                        val userId = interaction.userId
                        val streamId = interaction.livestreamId
                        Log.i("RaydioInteraction", "User $userId stops playing livestream $streamId")
                    }
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideRaydioSDK(
        preferences: SharedPreferences,
        credentials: RaydioSDKCredentials,
        logger: RaydioLogger
    ): RaydioSDK {
        return RaydioSDKBuilder(credentials, preferences).setLogger(logger).build()
    }

    @Provides
    @Singleton
    fun provideRaydioConnection(
        sdk: RaydioSDK
    ): RaydioConnection {
        return sdk.connection
    }

    @Provides
    @Singleton
    fun provideLivestreamLoader(
        sdk: RaydioSDK
    ): RaydioLivestreamLoader {
        return sdk.livestreamLoader
    }

    @Provides
    @Singleton
    fun provideLivestreamContainer(
        sdk: RaydioSDK
    ): RaydioLivestreamGroupContainer {
        return sdk.livestreamGroupContainer
    }

    @Provides
    @Singleton
    fun provideLivestreamPlayerFactory(
        sdk: RaydioSDK
    ): RaydioLivestreamPlayer.Factory {
        return sdk.livestreamPlayerFactory
    }

    @Provides
    @Singleton
    fun providePlayStateContainer(): PlayStateContainer {
        return MainPlayStateContainer()
    }
}