package com.desponsor.core.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

/** Real [AudioEngine] backed by Media3's ExoPlayer, playing the bundled sample audio (research.md §5). */
class Media3AudioEngine(
    context: Context,
) : AudioEngine {
    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext).build()
    private val packageName = context.packageName

    override fun setSource(sampleAudioRef: String) {
        val uri = Uri.parse("android.resource://$packageName/raw/$sampleAudioRef")
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }

    override fun play() {
        player.play()
    }

    override fun pause() {
        player.pause()
    }

    override fun seekTo(positionSeconds: Int) {
        player.seekTo(positionSeconds * 1000L)
    }

    override fun currentPositionSeconds(): Int = (player.currentPosition / 1000L).toInt()

    fun release() {
        player.release()
    }
}
