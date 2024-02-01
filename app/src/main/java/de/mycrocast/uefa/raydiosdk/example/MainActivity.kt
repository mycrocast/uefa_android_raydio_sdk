package de.mycrocast.uefa.raydiosdk.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import de.mycrocast.uefa.raydiosdk.example.connection.ConnectingScreen
import de.mycrocast.uefa.raydiosdk.example.connection.ConnectionFailedScreen
import de.mycrocast.uefa.raydiosdk.example.connection.DisconnectedScreen
import de.mycrocast.uefa.raydiosdk.example.livestream.list.LivestreamListScreen
import de.mycrocast.uefa.raydiosdk.example.ui.theme.RaydioSDKExampleTheme

/**
 * Entry point of the application.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RaydioSDKExampleTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Navigates to different screens depending of the UIState given by the ViewModel.
 *
 * @param viewModel ViewModel which adjusts the UIState according to RaydioConnection state.
 */
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    val screen = uiState.value.screen

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        screen?.let {
            when (it) {
                MainViewModel.Screen.CONNECTION_FAILED -> ConnectionFailedScreen()
                MainViewModel.Screen.CONNECTING -> ConnectingScreen()
                MainViewModel.Screen.LIVESTREAMS -> LivestreamListScreen()
                MainViewModel.Screen.DISCONNECTED -> DisconnectedScreen()
            }
        }
    }
}