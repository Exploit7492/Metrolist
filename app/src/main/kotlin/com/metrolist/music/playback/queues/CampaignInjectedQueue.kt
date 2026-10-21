/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.playback.queues

import androidx.media3.common.MediaItem
import com.mavins.campaign.CampaignCard
import com.mavins.campaign.Campaigns
import com.mavins.campaign.QueueInjector
import com.metrolist.music.extensions.toMediaItem
import com.metrolist.music.models.MediaMetadata

/**
 * A [Queue] decorator that splices a Mavins campaign into every fifth
 * position of any underlying queue (list, radio, playlist, album, autoplay).
 *
 * ## The rule
 * Base items are taken in runs of four; one campaign follows each run, so
 * campaigns land at indices 4, 9, 14, … — the same shape Velune, Flow,
 * BitChord and LastWave build. The run rule carries across pages, so a page
 * boundary in a paginated queue is invisible in the output.
 *
 * ## Why the fetch is once per slot
 * The queue-slot call marks the chosen campaign served in the same transaction
 * that selects it. Fetching a batch up front would mark campaigns served before
 * they reached a real slot, corrupting the fair rotation for every other
 * listener. [QueueInjector] fetches once per slot; this class only decides where
 * the items go.
 *
 * ## Index adjustment
 * When the user taps a song at index N, the injected campaigns before it shift
 * it right. The adjusted index is tracked during the same splice pass rather
 * than recomputed with a formula, because a slot whose fetch returns null
 * (nothing eligible right now) would make a formula undercount and start
 * playback on the wrong song.
 *
 * @param baseQueue the app's own queue, untouched except for the injected items.
 * @param genre null for a queue with no genre context; a concrete term confines
 *   the pick to that genre and its neighbours.
 */
class CampaignInjectedQueue(
    private val baseQueue: Queue,
    private val genre: String? = null,
) : Queue {

    private val injector: QueueInjector = Campaigns.queueInjector()

    /** How many base items have been consumed across all pages. */
    private var baseItemsConsumed: Int = 0

    override val preloadItem: MediaMetadata? = baseQueue.preloadItem

    override suspend fun getInitialStatus(): Queue.Status {
        baseItemsConsumed = 0
        val baseStatus = baseQueue.getInitialStatus()
        baseItemsConsumed = baseStatus.items.size

        val result =
            injector.inject(
                baseItems = baseStatus.items,
                genre = genre,
                offset = 0,
                targetOriginalIndex = baseStatus.mediaItemIndex,
            ) { campaign -> campaign.toCampaignMediaItem() }

        return Queue.Status(
            title = baseStatus.title,
            items = result.items,
            mediaItemIndex = result.adjustedTargetIndex.takeIf { it >= 0 } ?: baseStatus.mediaItemIndex,
            position = baseStatus.position,
        )
    }

    override fun hasNextPage(): Boolean = baseQueue.hasNextPage()

    override suspend fun nextPage(): List<MediaItem> {
        val chunk = baseQueue.nextPage()
        val result =
            injector.inject(
                baseItems = chunk,
                genre = genre,
                offset = baseItemsConsumed,
            ) { campaign -> campaign.toCampaignMediaItem() }
        baseItemsConsumed += chunk.size
        return result.items
    }
}

/** Extras key a campaign [MediaItem] carries its campaign id under. */
const val CAMPAIGN_ID_KEY: String = "mavins_campaign_id"

/** The campaign id a [MediaItem] was injected for, or null for an ordinary track. */
val MediaItem.campaignIdOrNull: String?
    get() = this.mediaMetadata.extras?.getString(CAMPAIGN_ID_KEY)

/**
 * A campaign as a playable [MediaItem]. The campaign id is stashed in the
 * item's extras so the play reporter can attribute the play without the app
 * having to remember which item was promoted.
 */
fun CampaignCard.toCampaignMediaItem(): MediaItem {
    val metadata =
        MediaMetadata(
            id = songId,
            title = title,
            artists = listOf(MediaMetadata.Artist(id = null, name = artist)),
            duration = 0,
            thumbnailUrl = thumbnailUrl.ifBlank { null },
        )
    val built = metadata.toMediaItem()
    // Extras live on the item's MediaMetadata, not on MediaItem.Builder — there
    // is no MediaItem.Builder.setExtras. Merge rather than replace: the app's own
    // toMediaItem already put the artwork uri and explicit flag in there.
    val extras = (built.mediaMetadata.extras ?: android.os.Bundle()).apply {
        putString(CAMPAIGN_ID_KEY, id)
    }
    return built.buildUpon()
        .setMediaMetadata(built.mediaMetadata.buildUpon().setExtras(extras).build())
        .build()
}
