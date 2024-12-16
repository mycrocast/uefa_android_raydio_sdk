package de.mycrocast.uefa.raydiosdk.example.livestream.group.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.mycrocast.android.play_by_ear.sdk.core.domain.PlayByEarLivestream
import de.mycrocast.uefa.raydiosdk.example.R
import kotlinx.coroutines.launch

/**
 * Used to display all livestreams of a livestream group, from which the user can select on to start playing.
 *
 * @param bottomSheetState Current state of the bottom sheet.
 * @param onLivestreamClicked Invoked whenever the user selected a livestream to play.
 * @param onDismiss Invoked when the bottom sheet is dismissed (either after or without a selection of a livestream)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivestreamGroupBottomSheet(
    bottomSheetState: LivestreamGroupListViewModel.BottomSheetState,
    onLivestreamClicked: (PlayByEarLivestream) -> Unit,
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // show the bottom sheet only if in show state
    if (bottomSheetState is LivestreamGroupListViewModel.BottomSheetState.Show) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState
        ) {
            // display a RaydioLivestreamRow for each livestream
            Column(Modifier.padding(8.dp)) {
                bottomSheetState.group.livestreams.forEach { stream ->
                    LivestreamBottomSheetRow(stream) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onLivestreamClicked(stream)
                                onDismiss()
                            }
                        }
                    }
                }
            }

            // lifts the bottom sheet a little bit from the bottom of the screen
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

/**
 * Row displaying a play icon and the language of the livestream.
 *
 * @param livestream The livestream to display in this row.
 * @param onClick Invoked when the user clicked on this row.
 */
@Composable
fun LivestreamBottomSheetRow(
    livestream: PlayByEarLivestream,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick.invoke() },
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = CardDefaults.outlinedShape
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = R.drawable.ic_start_play),
                contentDescription = null
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                text = livestream.language.native,
                textAlign = TextAlign.Center
            )
        }
    }
}
