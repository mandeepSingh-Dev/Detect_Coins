package com.example.detectcoins.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

class AudioRecorder(val context:Context, val audioCallback:AudioCallback)  {

    private val audioSource = MediaRecorder.AudioSource.DEFAULT
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private val sampleRate = 44100

      var  recorder : AudioRecord? = null
    private val minBufferSize : Int by lazy {
        AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding)
    }

    private var audioCallbackk = audioCallback

    fun setCallback(callback : AudioCallback){
        audioCallbackk = callback
    }



    suspend fun start(context: Context) = withContext(Dispatchers.IO){
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)

        val buffer = ByteArray(minBufferSize)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) { }
        recorder = AudioRecord(audioSource,sampleRate,channelConfig,audioEncoding,minBufferSize)

        recorder?.startRecording()

        Log.d("dfidnfdf",recorder?.state.toString())



        while( recorder?.read(buffer,0,minBufferSize)!! > 0)
        {
            Log.d("dfidnfdf",buffer.last().toString())
            audioCallback.onBufferAvailable(buffer)
        }

       // recorder.stop()
        //recorder.release()

        return@withContext recorder?.state

    }

    fun stop() {
        if (recorder != null) {
            recorder?.stop()
            recorder?.release()
        }
    }

    fun getRecorderState() = recorder?.state


}