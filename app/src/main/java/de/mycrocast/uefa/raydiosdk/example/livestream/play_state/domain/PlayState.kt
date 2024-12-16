package de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain

/**
 * Represents the current play state of a livestream.
 *
 * @property streamToken The identifier of the livestream.
 */
sealed class PlayState(val streamToken: String) {

    /**
     * The process to establish a connection to an audio broadcast of the livestream is currently running.
     */
    class Connecting(streamToken: String) : PlayState(streamToken)

    /**
     * An audio broadcast of the livestream is currently playing.
     */
    class Playing(streamToken: String) : PlayState(streamToken)
}