package com.example.kokoro82m.utils

import android.media.AudioFormat
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioManager
import android.media.AudioAttributes
import android.media.AudioTrack
//import androidx.privacysandbox.tools.core.generator.build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun playAudio(audioData: FloatArray, scope: CoroutineScope, onComplete: () -> Unit, onStart: () -> Unit) {
    scope.launch(Dispatchers.IO) {
        val sampleRate = 22050
        val channelConfig = CHANNEL_OUT_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat)

//        val audioTrack = AudioTrack(
//            AudioManager.STREAM_MUSIC,
//            sampleRate,
//            channelConfig,
//            audioFormat,
//            bufferSize,
//            AudioTrack.MODE_STREAM
//        )
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        val audioTrack = AudioTrack(
            audioAttributes,
            AudioFormat.Builder()
                .setEncoding(audioFormat)
                .setSampleRate(sampleRate)
                .setChannelMask(channelConfig)
                .build(),
            bufferSize,
            AudioTrack.MODE_STREAM,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        val byteBuffer = ByteBuffer.allocate(audioData.size * 2)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        val shortBuffer = byteBuffer.asShortBuffer()

        for (sample in audioData) {
            val pcmValue = (sample * Short.MAX_VALUE).toInt().toShort()
            shortBuffer.put(pcmValue)
        }

        audioTrack.play()
//        onStart()
        audioTrack.write(byteBuffer.array(), 0, byteBuffer.array().size)

        audioTrack.release()

        withContext(Dispatchers.Main) {
            onComplete()
        }
    }
}
