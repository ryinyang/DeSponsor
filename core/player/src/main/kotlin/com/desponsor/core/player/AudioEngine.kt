package com.desponsor.core.player

/** The mechanics of playing one audio source — deliberately small so it's easy to fake in tests. */
interface AudioEngine {
    fun setSource(sampleAudioRef: String)

    fun play()

    fun pause()

    fun seekTo(positionSeconds: Int)

    fun currentPositionSeconds(): Int
}
