package com.cds.childrensmall.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.cds.childrensmall.ui.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecordingUtil(val activity: AppCompatActivity) {

    private val TAG = "AudioRecordDemo"
    private val SAMPLE_RATE = 44100
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE =
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    private var mAudioRecord: AudioRecord? = null
    private var mIsRecording = false
    private var mOutputFile: File? = null

    private val permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            startRecording()
        }

    }

    fun startRecording() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch( Manifest.permission.RECORD_AUDIO)
            return
        }
        mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        )
        mOutputFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/recording.pcm")
        mIsRecording = true
        mAudioRecord!!.startRecording()
        Thread {
            try {
                val outputStream = FileOutputStream(mOutputFile)
                val buffer = ByteArray(BUFFER_SIZE)
                while (mIsRecording) {
                    val bytesRead = mAudioRecord!!.read(buffer, 0, BUFFER_SIZE)
                    outputStream.write(buffer, 0, bytesRead)
                }
                outputStream.close()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to write audio data to file: " + e.message)
            }
        }.start()
    }

    fun stopRecording() {
        if (mAudioRecord != null) {
            mIsRecording = false
            mAudioRecord!!.stop()
            mAudioRecord!!.release()
            mAudioRecord = null
        }
    }
}

