package de.mycrocast.uefa.raydiosdk.example.livestream.play_state.data

import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayState
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayStateContainer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

/**
 * Main safe implementation of the PlayStateContainer.
 *
 * @property dispatcher The CoroutineDispatcher to use in which operations are executed.
 */
class MainPlayStateContainer(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : PlayStateContainer {

    private val _currentPlayState = MutableStateFlow<PlayState?>(null)
    override val currentPlayState = _currentPlayState.asStateFlow()

    override suspend fun onConnect(livestreamId: String) = withContext(dispatcher) {
        _currentPlayState.update { PlayState.Connecting(livestreamId) }
    }

    override suspend fun onPlay(livestreamId: String) = withContext(dispatcher) {
        _currentPlayState.update { PlayState.Playing(livestreamId) }
    }

    override suspend fun onDisconnect(livestreamId: String) {
        _currentPlayState.update { PlayState.Connecting(livestreamId) }
    }

    override suspend fun onStop() = withContext(dispatcher) {
        _currentPlayState.update { null }
    }
}