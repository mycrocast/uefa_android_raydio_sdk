package de.mycrocast.uefa.raydiosdk.example.livestream.group.domain

import de.mycrocast.android.play_by_ear.sdk.core.domain.PlayByEarLivestream

data class LivestreamGroup(
    val title: String,
    val matchId: String?,
    val livestreams: List<PlayByEarLivestream>
)
