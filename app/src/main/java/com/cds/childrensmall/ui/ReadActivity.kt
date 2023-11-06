package com.cds.childrensmall.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cds.childrensmall.R
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.common.readBackeBtn
import com.cds.childrensmall.common.readGoAnswerBtn
import com.cds.childrensmall.common.readNextVoiceBtn
import com.cds.childrensmall.common.readRecordOkScore
import com.cds.childrensmall.common.readScore
import com.cds.childrensmall.common.readStartReadBtn
import com.cds.childrensmall.databinding.ActivityReadBinding
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.util.net.DataHandler
import com.cds.childrensmall.util.totalReadScore
import com.cds.childrensmall.viewmodel.ReadViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ReadActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "AudioRecordDemo"
    private val SAMPLE_RATE = 16000
   // private val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE =
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    private var mAudioRecord: AudioRecord? = null
    private var mIsRecording = false
    private var mOutputFile: File? = null
    private lateinit var mBinding: ActivityReadBinding

    private var isRecoding = true
    private val mViewModel: ReadViewModel by viewModel()
    private var path = ""//录音文件位置
    private var curContent: ConfigDataBean.ContentData?=null //配置数据
    private var genduList = ArrayList<ConfigDataBean.GenDuBean>()//跟读列表
    private var curPosition = 0//跟读的position
    private var curProcess = 0 //当前进度
    private var errorCount = 0//读错次数  只能重读两次

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        startRecording()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding  = DataBindingUtil.setContentView(this,R.layout.activity_read)
        EventBus.getDefault().register(this)
        initView()
        initListener()
    }

    private fun initListener() {
        mBinding.backBtn.setOnClickListener(this)
        mBinding.nextBtn.setOnClickListener(this)
    }

    private fun initView() {
        totalReadScore = 0
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
        curContent?.genDu?.let {
            genduList.addAll(it)
        }
    }


    override fun onClick(v: View?) {

        when(v){
            mBinding.recordBtn->{//录制

                isRecoding = if (isRecoding){
                    startRecording()
                    false
                }else{
                    stopRecording()
                    true
                }

            }

            mBinding.backBtn->{//返回
                EventBus.getDefault().post(readBackeBtn)
                this@ReadActivity.finish()
            }

            mBinding.nextBtn->{//下一段
                doNextFun()
            }
        }
    }

    /**
     * 下一段按钮
     */
    private fun doNextFun(){
//        if (curPosition<genduList.size){
//            EventBus.getDefault().post(readNextVoiceBtn)
//        }else{
            EventBus.getDefault().post(readGoAnswerBtn)
            val intent = Intent(this@ReadActivity, QuestionActivity::class.java)
            intent.putExtra("curContent", curContent)
            startActivity(intent)
            this@ReadActivity.finish()
//        }
    }


    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(
                this@ReadActivity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(
                this@ReadActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            &&ActivityCompat.checkSelfPermission(
                this@ReadActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
               )
            return
        }


        mAudioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        )

        path = if (Build.VERSION.SDK_INT>=29){
            getExternalFilesDir(null)?.absolutePath + "/recording.pcm"
        } else{
            Environment.getExternalStorageDirectory().absolutePath + "/recording.pcm"
        }
        mOutputFile =
            File(path)
        Log.i(TAG,"保存地址：${path}")
        mIsRecording = true
        mAudioRecord!!.startRecording()
        mBinding.recordBtn.setImageResource(R.mipmap.reconder_icon)
        Thread {
            try {
                val outputStream = FileOutputStream(mOutputFile)
                val buffer = ByteArray(BUFFER_SIZE)
                while (mIsRecording) {
                    val bytesRead = mAudioRecord!!.read(buffer, 0, BUFFER_SIZE)
                    outputStream.write(buffer, 0, bytesRead)
                }
                Log.i(TAG,"录制完成！")
                outputStream.close()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to write audio data to file: " + e.message)
            }
        }.start()
        curProcess = 5000
        val myCountIme = MyCounterTime(5000L,50L)
        myCountIme.start()
    }

   private fun stopRecording() {
       mBinding.recordBtn.setImageResource(R.mipmap.reconder_close_icon)
       EventBus.getDefault().post(readRecordOkScore)
        if (mAudioRecord != null) {
            mIsRecording = false
            mAudioRecord!!.stop()
            mAudioRecord!!.release()
            mAudioRecord = null
        }
        Log.i("11","-->录制完成")
      // asrAudioToTextFun(path,"recording.pcm")
       getReadSoreFun(path,"recording.pcm")
    }


    /**
     * 获取跟读评分
     */
    private fun getReadSoreFun(filePath:String,filename:String){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@ReadActivity,
                block = {
                    mViewModel.getReadSoreFun(filePath,filename,genduList[curPosition].id?:"","1858101602711")
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    Log.i("11","--->成功${it}")
                    Log.i("11","--->跟读id${genduList[curPosition].id?:""}")
                    val mScore = it?:0
//                    if (){ //为1的时候重读，并标记次数
//                        errorCount ++
//                    }else{//大于1时，直接到下一步
//                        totalReadScore += it?:0
//                        errorCount =0
//                    }
//                    if (errorCount >3){ //第四次归零 实际正常一遍 复读两遍
//                        errorCount =0
//                    }

                    if (mScore>1){//分数大于1
                        errorCount = 0
                        totalReadScore += mScore
                    } else if (errorCount==3){ //复读完两次
                        totalReadScore += mScore
                    }else  {//没到两次复读
                        errorCount ++
                    }

                    EventBus.getDefault().post("${readScore}?${it?:0}?$errorCount")
                    if (errorCount==0||errorCount==3){
                        errorCount =0
                        val waitCounterTime = WaitCounterTime(4000L,1000L)
                        waitCounterTime.start()
                    }

                }
            )
        }
    }


    /**
     * 语音转文字
     */
    private fun asrAudioToTextFun(filePath:String,filename:String){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@ReadActivity,
                block = {
                    mViewModel.getASRFun(filePath,filename,"1858101602711")
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    Log.i("11","--->成功${it}")

                }
            )
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    /**
     * 接收点击信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        //Log.i("11","--->service ${msg}")
        when(msg){
            readStartReadBtn ->{//开始录音
                startRecording()
            }

        }
    }

    inner class MyCounterTime(var totalTime:Long,var countInterval:Long ):CountDownTimer(totalTime,countInterval){
        override fun onTick(millisUntilFinished: Long) {
            curProcess-=countInterval.toInt()
            mBinding.progress.progress = curProcess
        }

        override fun onFinish() {
            mBinding.progress.progress = 0
            curProcess= 0
            stopRecording()
        }
    }

    /**
     * 用于评分结束后下一个视频播放
     */
    inner class WaitCounterTime(var totalTime:Long,var countInterval:Long ):
        CountDownTimer(totalTime,countInterval){
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {
            if (curPosition<genduList.size){
                curPosition ++
            }
            EventBus.getDefault().post(readNextVoiceBtn)
        }
    }

}