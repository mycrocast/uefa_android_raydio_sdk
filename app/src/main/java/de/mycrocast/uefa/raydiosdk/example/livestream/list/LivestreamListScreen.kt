package de.mycrocast.uefa.raydiosdk.example.livestream.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Screen displaying all currently active RaydioLivestreamGroups as well as current PlayState, current loading process.
 *
 * @param viewModel Contains the current UIState and a possibility to reload currently active livestreams. Starts & stops Foreground services.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivestreamListScreen(
    viewModel: LivestreamListViewModel = hiltViewModel()
) {
    // current UIState, given by ViewModel
    val uiState = viewModel.uiState.collectAsState()
    val isLoading = uiState.value.isLoading
    val isRefreshing = uiState.value.isRefreshing
    val livestreamGroups = uiState.value.livestreamGroups
    val bottomSheetState = uiState.value.bottomSheetState
    val playState = uiState.value.playState

    // current pull refresh state
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.onRefreshList()
        }
    }

    // "binds" pull refresh animation to our isRefreshing state
    if (isRefreshing) {
        pullRefreshState.startRefresh()
    } else {
        pullRefreshState.endRefresh()
    }

    // show an initial loading animation in the center and nothing else if initial loading
    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize(0.5f))
        return
    }

    // display the list of livestream groups
    // add option to user for pull refresh
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .nestedScroll(pullRefreshState.nestedScrollConnection)
                .padding(innerPadding)
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(livestreamGroups) { group ->
                    LivestreamItem(
                        livestreamGroup = group,
                        playState
                    ) {
                        viewModel.onRaydioLivestreamGroupClicked(group)
                    }
                }
            }

            if (livestreamGroups.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "There are currently no livestreams available.",
                        textAlign = TextAlign.Center
                    )
                }
            }

            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullRefreshState
            )
        }
    }

    // Bottom sheet for user to select a livestream of a selected livestream group he wants to start playing.
    RaydioLivestreamGroupBottomSheet(
        bottomSheetState = bottomSheetState,
        onLivestreamClicked = { viewModel.onRaydioLivestreamClicked(it) },
        onDismiss = { viewModel.onBottomSheetDismissed() }
    )
}