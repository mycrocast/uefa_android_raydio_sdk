package de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain

import kotlinx.coroutines.flow.StateFlow

/**
 * Holds and adjust the current play state.
 */
interface PlayStateContainer {

    /**
     * Current play state.
     * Always null, if nothing is currently connecting nor playing. (Therefore it is also null in the beginning.)
     */
    val currentPlayState: StateFlow<PlayState?>

    /**
     * Changes the current play state to Connecting.
     *
     * @param livestreamId The identifier of the connecting livestream.
     */
    suspend fun onConnect(livestreamId: String)

    /**
     * Changes the current play state to Playing.
     *
     * @param livestreamId The identifier of the playing livestream.
     */
    suspend fun onPlay(livestreamId: String)

    /**
     * Changes the current play state to Connecting.
     *
     * @param livestreamId The identifier of the disconnected livestream.
     */
    suspend fun onDisconnect(livestreamId: String)

    /**
     * Sets the current play state to null.
     */
    suspend fun onStop()
}