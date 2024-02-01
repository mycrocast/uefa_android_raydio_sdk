package de.mycrocast.uefa.raydiosdk.example.livestream.list

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.mycrocast.raydio.uefa.sdk.livestream.domain.RaydioLivestreamGroup
import de.mycrocast.uefa.raydiosdk.example.R
import de.mycrocast.uefa.raydiosdk.example.livestream.play_state.domain.PlayState

/**
 * Represents a livestream group. Contains a PlayButton, which indicates the current play state:
 * - Not Playing -> Play Icon
 * - Connecting -> Loading animation
 * - Playing -> Stop Icon.
 *
 * @param livestreamGroup The group of RaydioLivestreams to display.
 * @param playState The current play state.
 * @param onClick Action to invoke, when the item was clicked by the user.
 * @receiver
 */
@Composable
fun LivestreamItem(
    livestreamGroup: RaydioLivestreamGroup,
    playState: PlayState?,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick.invoke() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {

            // do we have a active play state for a livestream of our group?
            if (playState != null && livestreamGroup.livestreams.any { it.id == playState.streamId }) {

                // are we currently connecting to a livestream of this group?
                if (playState is PlayState.Connecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp)
                    )
                }

                // are we currently playing a livestream of this group?
                if (playState is PlayState.Playing) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_stop_play),
                        modifier = Modifier.size(48.dp),
                        contentDescription = null
                    )
                }
            } else {
                // we have either currently no connecting/playing livestream or the on which is connecting/playing is of another group.
                Icon(
                    painter = painterResource(id = R.drawable.ic_start_play),
                    modifier = Modifier.size(48.dp),
                    contentDescription = null
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(16.dp)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                text = livestreamGroup.title
            )
        }
    }
}