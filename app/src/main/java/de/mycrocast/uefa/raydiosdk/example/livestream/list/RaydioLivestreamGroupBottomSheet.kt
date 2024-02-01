package de.mycrocast.uefa.raydiosdk.example.livestream.list

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
import de.mycrocast.raydio.uefa.sdk.livestream.domain.RaydioLivestream
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
fun RaydioLivestreamGroupBottomSheet(
    bottomSheetState: LivestreamListViewModel.BottomSheetState,
    onLivestreamClicked: (RaydioLivestream) -> Unit,
    onDismiss: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // show the bottom sheet only if in show state
    if (bottomSheetState is LivestreamListViewModel.BottomSheetState.Show) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState
        ) {
            // display a RaydioLivestreamRow for each livestream
            Column(Modifier.padding(8.dp)) {
                bottomSheetState.group.livestreams.forEach { stream ->
                    RaydioLivestreamRow(stream) {
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
fun RaydioLivestreamRow(
    livestream: RaydioLivestream,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick.invoke() },
        modifier = Modifier.fillMaxWidth(),
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
