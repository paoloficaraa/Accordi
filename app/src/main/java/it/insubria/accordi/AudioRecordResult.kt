package it.insubria.accordi

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import timber.log.Timber
import android.content.pm.PackageManager
import android.Manifest
import android.content.Context
import androidx.core.content.ContextCompat

private val RECORDER_CHANNELS = shortArrayOf(AudioFormat.CHANNEL_IN_MONO.toShort(), AudioFormat.CHANNEL_IN_STEREO.toShort())
private val RECORDER_AUDIO_FORMATS = shortArrayOf(AudioFormat.ENCODING_PCM_16BIT.toShort(), AudioFormat.ENCODING_PCM_8BIT.toShort())
private val RECORDER_SAMPLE_RATES = intArrayOf(8000, 11025, 22050, 44100)
data class AudioRecordResult(val audioRecord: AudioRecord, val format: TarsosDSPAudioFormat, val bufferSize: Int)

fun initAudioRecord(context: Context): AudioRecordResult? {
    for (rate in RECORDER_SAMPLE_RATES.reversed()) {
        for (audioFormat in RECORDER_AUDIO_FORMATS) {
            for (channelConfig in RECORDER_CHANNELS) {
                Timber.d("Provando a creare AudioRecord con rate $rate, formato $audioFormat e canale $channelConfig")
                try {
                    val bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig.toInt(), audioFormat.toInt())
                    val bytesPerElement = if (audioFormat == AudioFormat.ENCODING_PCM_8BIT.toShort()) 8 else 16
                    val channels = if (channelConfig == AudioFormat.CHANNEL_IN_MONO.toShort()) 1 else 2
                    val signed = true
                    val bigEndian = false
                    if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            val recorder = AudioRecord(AudioSource.DEFAULT, rate, channelConfig.toInt(), audioFormat.toInt(), bufferSize)
                            if(recorder.state == AudioRecord.STATE_INITIALIZED) {
                                Timber.d("AudioRecord creato con rate $rate, formato $audioFormat e canale $channelConfig")
                                return AudioRecordResult(recorder, TarsosDSPAudioFormat(rate.toFloat(), bytesPerElement, channels, signed, bigEndian), bufferSize)
                            }
                        } else {
                            // Gestisci il caso in cui il permesso non è stato concesso
                            Timber.e("Permesso di registrazione audio non concesso")
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Errore durante la creazione di AudioRecord")
                }
            }
        }
    }
    return null
}
