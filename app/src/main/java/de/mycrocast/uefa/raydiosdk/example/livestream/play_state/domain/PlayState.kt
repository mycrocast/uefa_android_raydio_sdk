package de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain

/**
 * Represents the current play state of a livestream.
 *
 * @property streamId The identifier of the livestream.
 */
sealed class PlayState(val streamId: String) {

    /**
     * The process to establish a connection to an audio broadcast of the livestream is currently running.
     */
    class Connecting(streamId: String) : PlayState(streamId)

    /**
     * An audio broadcast of the livestream is currently playing.
     */
    class Playing(streamId: String) : PlayState(streamId)
}