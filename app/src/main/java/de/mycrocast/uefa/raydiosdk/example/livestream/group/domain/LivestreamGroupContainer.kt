package de.mycrocast.uefa.raydiosdk.example.livestream.group.domain

import de.mycrocast.android.play_by_ear.sdk.livestream.container.domain.PlayByEarLivestreamContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LivestreamGroupContainer(
    livestreamContainer: PlayByEarLivestreamContainer
) {

    val online: Flow<List<LivestreamGroup>> = livestreamContainer.online.map { streams ->
        val result = mutableListOf<LivestreamGroup>()
        val groups = streams.groupBy { LivestreamGroupingKey(it.title, it.matchId) }

        for (entries in groups.entries) {
            val group = LivestreamGroup(
                title = entries.key.title,
                matchId = entries.key.matchId,
                livestreams = entries.value
            )

            result.add(group)
        }

        result.sortBy { it.title }
        return@map result
    }
}