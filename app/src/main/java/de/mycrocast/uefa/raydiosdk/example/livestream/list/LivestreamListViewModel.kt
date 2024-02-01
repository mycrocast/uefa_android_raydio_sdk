package de.mycrocast.uefa.raydiosdk.example.livestream.list

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import de.mycrocast.raydio.uefa.sdk.livestream.container.domain.RaydioLivestreamGroupContainer
import de.mycrocast.raydio.uefa.sdk.livestream.domain.RaydioLivestream
import de.mycrocast.raydio.uefa.sdk.livestream.domain.RaydioLivestreamGroup
import de.mycrocast.raydio.uefa.sdk.livestream.loader.domain.RaydioLivestreamLoader
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayState
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayStateContainer
import de.mycrocast.uefa.raydiosdk.example.livestream.service.LivestreamPlayService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel controlling and observing the livestream group loading process (initial & refresh). Observes the container for livestream groups for changes.
 * Redirects changes to the UI via UIState flow.
 *
 * @property loader Used to initial and refresh load currently active livestream groups.
 * @property container Used to observe changes of all currently active livestream groups.
 * @property playStateContainer Used to observe changes of the current play state of a livestream.
 * @property context Used to start the foreground service for playing a livestream.
 */
@HiltViewModel
class LivestreamListViewModel @Inject constructor(
    private val loader: RaydioLivestreamLoader,
    private val container: RaydioLivestreamGroupContainer,
    private val playStateContainer: PlayStateContainer,
    @ApplicationContext private val context: Context
) : ViewModel() {

    /**
     * Represents the state the bottom sheet where the user can select a livestream to play.
     */
    sealed interface BottomSheetState {

        /**
         * No bottom sheet is displayed.
         */
        data object Hide : BottomSheetState

        /**
         * A bottom sheet is displayed.
         *
         * @property group The group in which the user can select a livestream of to play.
         */
        data class Show(val group: RaydioLivestreamGroup) : BottomSheetState
    }

    /**
     * Represents the current state of the user interface
     */
    data class UIState(
        /**
         * Whether the initial loading of livestreams process in running or not
         */
        val isLoading: Boolean = false,

        /**
         * Whether a reloading process of livestreams is running or not
         */
        val isRefreshing: Boolean = false,

        /**
         * Current play state
         */
        val playState: PlayState? = null,

        /**
         * Currently active groups of livestreams
         */
        val livestreamGroups: List<RaydioLivestreamGroup> = emptyList(),

        /**
         * Current state of the bottom sheet
         */
        val bottomSheetState: BottomSheetState = BottomSheetState.Hide
    )

    private val _uiState = MutableStateFlow(UIState())
    val uiState = _uiState.asStateFlow()

    init {
        // collect changes of the currently active livestream groups
        viewModelScope.launch {
            container.online.collect { streams ->

                // adjust bottom sheet according to changes:
                // - if the bottom sheet is showing and the group was removed (because all of its livestreams ended), hide the bottom sheet
                // - if the bottom sheet is showing and the currently active shown group has changed, update bottom sheet
                var newSheetState: BottomSheetState = BottomSheetState.Hide
                val sheetState = uiState.value.bottomSheetState
                if (sheetState is BottomSheetState.Show) {
                    val group = sheetState.group
                    val updated = streams.find { it.title == group.title }
                    if (updated != null) {
                        newSheetState = BottomSheetState.Show(updated)
                    }
                }

                // update ui state accordingly
                _uiState.update {
                    it.copy(
                        livestreamGroups = streams,
                        bottomSheetState = newSheetState
                    )
                }
            }
        }

        // start initial loading process
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val success = loader.load()
            if (!success) {
                // TODO: add failure info for user
            }

            _uiState.update { it.copy(isLoading = false) }
        }

        // collect updates for current play state
        viewModelScope.launch {
            playStateContainer.currentPlayState.collect { newState ->
                _uiState.update { it.copy(playState = newState) }
            }
        }
    }

    /**
     * Starts the reloading process of currently active livestream groups.
     */
    fun onRefreshList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val success = loader.load()
            if (!success) {
                // TODO: add failure info for user
            }

            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    /**
     * User clicked on a livestream group.
     *
     * @param group The clicked group.
     */
    fun onRaydioLivestreamGroupClicked(group: RaydioLivestreamGroup) {
        viewModelScope.launch {
            // if we are currently playing a livestream of this group, the user wants to stop playing
            val currentPlayState = uiState.value.playState
            if (currentPlayState != null && group.livestreams.any { it.id == currentPlayState.streamId }) {
                context.stopService(LivestreamPlayService.stop(context))
                return@launch
            }

            // else we show the bottom sheet where the user can select a livestream of this group to start playing
            _uiState.update { it.copy(bottomSheetState = BottomSheetState.Show(group)) }
        }
    }

    /**
     * User dismissed the bottom sheet.
     */
    fun onBottomSheetDismissed() {
        viewModelScope.launch {
            _uiState.update { it.copy(bottomSheetState = BottomSheetState.Hide) }
        }
    }

    /**
     * User selected a livestream in the bottom sheet to play.
     * Starts a new foreground service for playing and stops all previous play foreground services.
     *
     * @param livestream The livestream to play.
     */
    fun onRaydioLivestreamClicked(livestream: RaydioLivestream) {
        // if a livestream is currently connecting/playing/disconnected, we need to stop the foreground service 8which stops the playing)
        if (uiState.value.playState != null) {
            context.stopService(LivestreamPlayService.stop(context))
        }

        // start a new foreground service zo start playing the selected livestream
        val intent = LivestreamPlayService.newInstance(context, livestream)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}