# Guide for the migration of the Raydio-SDK to the new PlayByEar-SDK

## 1. Update dependencies
- Add dependency to PlayByEar (de.mycrocast.android.sdk:play_by_ear with current version 0.0.1)
- remove dependency from Raydio (de.mycrocast.uefa:raydio)

## 2. Migration

### 2.1 RaydioSDKCredentials
- Replace with the new PlayByEarSDKCredentials
- Instead of a 'clubId' this new credentials interface needs a 'token'
- This token will be provided by us to you (like the clubId in the previous SDK)

``` Before:
    @Provides
    @Singleton
    fun provideSDKCredentials(): RaydioSDKCredentials {
        return object : RaydioSDKCredentials {
            override val clubId: Long = ???
        }
    }
```

``` After:
    @Provides
    @Singleton
    fun provideSDKCredentials(): PlayByEarSDKCredentials {
        return object : PlayByEarSDKCredentials {
            override val token: String = ???
        }
    }
```

### 2.2 RaydioLogger
- Replace with the new PlayByEarLogger
- Remove the overridden function 'interaction' as it is no longer supported

``` Before:
    @Provides
    @Singleton
    fun provideSDKLogger(): RaydioLogger {
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
```

``` After:
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
```

### 2.3 RaydioSDK
- Replace with the new PlayByEarSDK
- For building the SDK replace RaydioSDKBuilder with the new PlayByEarSDKBuilder
- The PlayByEarSDKBuilder now has three arguments in the constructor, instead of attaching the logger via 'setLogger' in the old SDK, the logger will be given directly as optional constructor argument

``` Before:
    @Provides
    @Singleton
    fun provideSDK(
        preferences: SharedPreferences,
        credentials: RaydioSDKCredentials,
        logger: RaydioLogger
    ): RaydioSDK {
        return RaydioSDKBuilder(credentials, preferences).setLogger(logger).build()
    }
```

``` After:
    @Provides
    @Singleton
    fun provideSDK(
        preferences: SharedPreferences,
        credentials: PlayByEarSDKCredentials,
        logger: PlayByEarLogger
    ): PlayByEarSDK {
        return PlayByEarSDKBuilder(credentials, preferences, logger).build()
    }
```

### 2.4 RaydioConnection
- Replace with the new PlayByEarConnection

##### 2.4.1 Injection (via Dagger-Hilt)

``` Before:
    @Provides
    @Singleton
    fun provideConnection(
        sdk: RaydioSDK
    ): RaydioConnection {
        return sdk.connection
    }
```

``` After:
    @Provides
    @Singleton
    fun provideConnection(
        sdk: PlayByEarSDK
    ): PlayByEarConnection {
        return sdk.connection
    }
```

##### 2.4.2 Usage
- Replace all occurrences of RaydioConnection with PlayByEarConnection
- Replace all occurrences of RaydioConnection.State with PlayByEarConnection.State

### 2.5 RaydioLivestreamLoader
- Replace with the new PlayByEarLivestreamLoader

##### 2.5.1 Injection (via Dagger-Hilt)

``` Before:
    @Provides
    @Singleton
    fun provideLivestreamLoader(
        sdk: RaydioSDK
    ): RaydioLivestreamLoader {
        return sdk.livestreamLoader
    }
```

``` After:
    @Provides
    @Singleton
    fun provideLivestreamLoader(
        sdk: PlayByEarSDK
    ): PlayByEarLivestreamLoader {
        return sdk.livestreamLoader
    }
```

##### 2.5.2 Usage
- Replace all occurrences of RaydioLivestreamLoader with PlayByEarLivestreamLoader

### 2.6 RaydioLivestreamPlayer.Factory
- Replace with new PlayByEarLivestreamPlayer.Factory

##### 2.6.1 Injection (via Dagger-Hilt)

``` Before:
    @Provides
    @Singleton
    fun provideLivestreamPlayerFactory(
        sdk: RaydioSDK
    ): RaydioLivestreamPlayer.Factory {
        return sdk.livestreamPlayerFactory
    }
```

``` After:
    @Provides
    @Singleton
    fun provideLivestreamPlayerFactory(
        sdk: PlayByEarSDK
    ): PlayByEarLivestreamPlayer.Factory {
        return sdk.livestreamPlayerFactory
    }
```

##### 2.6.2 Usage
- Replace all occurrences of RaydioLivestreamPlayer.Factory with PlayByEarLivestreamPlayer.Factory
- Replace all occurrences of RaydioLivestreamPlayer with PlayByEarLivestreamPlayer
- Replace all occurrences of RaydioLivestreamPlayer.PlayState with PlayByEarLivestreamPlayer.State

-> Careful, additional migration step needed:<br>
Instead of having the 'streamId'-Property like in the old states RaydioLivestreamPlayer.PlayState.Connecting, RaydioLivestreamPlayer.PlayState.Playing and RaydioLivestreamPlayer.PlayState.Disconnected, the corresponding PlayStates of the new PlayByEarLivestreamPlayer are having the 'streamToken'-Property. (As both of them are of type string, no other adjustments should be needed.)