package com.example.escapealgebraico

import android.content.Context
import android.media.MediaPlayer

object SoundManager {
    private var bgMusic: MediaPlayer? = null

    fun playBackgroundMusic(context: Context) {
        if (bgMusic == null) {
            bgMusic = MediaPlayer.create(context, R.raw.bg_music)
            bgMusic?.isLooping = true
        }
        bgMusic?.start()
    }

    fun stopBackgroundMusic() {
        bgMusic?.pause()
    }

    fun playCorrectSound(context: Context) {
        MediaPlayer.create(context, R.raw.correct).start()
    }

    fun playWrongSound(context: Context) {
        MediaPlayer.create(context, R.raw.wrong).start()
    }

}
