package de.mycrocast.uefa.raydiosdk.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.mycrocast.raydio.uefa.sdk.connection.domain.RaydioConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel which starts the initial connect try to the given RaydioConnection.
 * It will also collect updates of the connection state for the app to navigate to the specific screen.
 *
 * @property connection RaydioConnection to observe and to connect to.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val connection: RaydioConnection
) : ViewModel() {

    /**
     * Represents the different screens where the app can navigate to, depending on the current connection state.
     */
    enum class Screen {
        /**
         * A connection attempt failed.
         * Show a screen with the possibility to reconnect via RaydioConnection.
         */
        CONNECTION_FAILED,

        /**
         * A connection process is currently running.
         * Show a screen should show a loading animation.
         */
        CONNECTING,

        /**
         * A connection was established successfully.
         * Show a screen where the list of currently active RaydioLivestreams is loaded and is displayed.
         */
        LIVESTREAMS,

        /**
         * A previously established connection was closed.
         * Show a screen with the possibility to reconnect via RaydioConnection.
         */
        DISCONNECTED
    }

    /**
     * Represents the current state of the user interface
     *
     * @property screen The current Screen to show.
     */
    data class UIState(
        val screen: Screen? = null
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    init {
        // collect connection changes, routes to screens accordingly
        viewModelScope.launch {
            connection.currentState.collect { state ->
                when (state) {
                    RaydioConnection.State.NEW -> connect()
                    RaydioConnection.State.CONNECTING -> _uiState.update { it.copy(screen = Screen.CONNECTING) }
                    RaydioConnection.State.CONNECTED -> _uiState.update { it.copy(screen = Screen.LIVESTREAMS) }
                    RaydioConnection.State.DISCONNECTED -> _uiState.update { it.copy(screen = Screen.DISCONNECTED) }
                }
            }
        }
    }

    /**
     * Tries to establish a connection via RaydioConnection.
     * Navigates to the Connection_Failed screen in case of failure.
     */
    private suspend fun connect() {
        val success = connection.connect()
        if (!success) {
            _uiState.update { it.copy(screen = Screen.CONNECTION_FAILED) }
        }
    }
}