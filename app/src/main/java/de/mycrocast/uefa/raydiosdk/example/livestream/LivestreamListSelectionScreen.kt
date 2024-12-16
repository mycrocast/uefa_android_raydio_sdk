package de.mycrocast.uefa.raydiosdk.example.livestream

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import de.mycrocast.uefa.raydiosdk.example.livestream.group.presentation.LivestreamGroupListScreen
import de.mycrocast.uefa.raydiosdk.example.livestream.list.LivestreamListScreen

@Composable
fun LivestreamListSelectionScreen() {
    val tabs = listOf("Single", "Group")
    var tabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }
        when (tabIndex) {
            0 -> LivestreamListScreen()
            1 -> LivestreamGroupListScreen()
        }
    }
}