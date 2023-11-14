package com.cds.childrensmall.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cds.childrensmall.model.bean.AsrMessageDataBean
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.bean.ScoreBean
import com.cds.childrensmall.model.repository.AsrRepository
import com.cds.childrensmall.model.repository.DataRepository
import com.cds.childrensmall.model.repository.LoginRepository

class ReadViewModel(

    private val asrRepository: AsrRepository
) : ViewModel() {


    suspend fun getReadSoreFun(filePath:String,filename:String,genDuId:String,sessionId:String):  ScoreBean?{
        return asrRepository.getReadSoreFun(filePath,filename,genDuId,sessionId)
    }

    /**
     * 语音转文字
     */
    suspend fun getASRFun(filePath:String,filename:String,sessionId:String):Any?{
        return asrRepository.asrAudioToTextFun(filePath,filename,sessionId)
    }


}