package com.cds.childrensmall.model.repository

import com.cds.childrensmall.model.bean.AsrMessageDataBean
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.datasource.AsrAudioToTextDatasource
import com.cds.childrensmall.model.datasource.GetConfigDatasource
import com.cds.childrensmall.model.datasource.GetReadScoreDatasource
import com.cds.childrensmall.model.datasource.GetSmsCodeDatasource
import com.cds.childrensmall.model.datasource.LoginDatasource


/**
 * 全局数据数据仓库
 */
class AsrRepository(
    private val asrAudioToTextDatasource: AsrAudioToTextDatasource,
    private val getReadScoreDatasource: GetReadScoreDatasource,
) {


    /**
     * 语音转文字
     *
     */
    suspend fun asrAudioToTextFun(filePath:String,filename:String): AsrMessageDataBean?{
        return  asrAudioToTextDatasource.load(filePath,filename)
    }


    /**
     *
     * 获取跟读评分
     */
    suspend fun getReadSoreFun(filePath:String,filename:String,genDuId:String,sessionId:String): Int?{
        return  getReadScoreDatasource.load(filePath,filename,genDuId,sessionId)
    }

}