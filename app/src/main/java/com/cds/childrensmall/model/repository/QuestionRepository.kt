package com.cds.childrensmall.model.repository

import com.cds.childrensmall.model.bean.AsrMessageDataBean
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.bean.StartAnswerBean
import com.cds.childrensmall.model.datasource.AsrAudioToTextDatasource
import com.cds.childrensmall.model.datasource.GetConfigDatasource
import com.cds.childrensmall.model.datasource.GetReadScoreDatasource
import com.cds.childrensmall.model.datasource.GetSmsCodeDatasource
import com.cds.childrensmall.model.datasource.LoginDatasource
import com.cds.childrensmall.model.datasource.NextQuestionDatasource
import com.cds.childrensmall.model.datasource.StartQuestionDatasource


/**
 * 问答数据数据仓库
 */
class QuestionRepository(
    private val startQuestionDatasource: StartQuestionDatasource,
    private val nextQuestionDatasource: NextQuestionDatasource,
) {

    /**
     *开始答题准备
     *@param code  如 Q&A
     *@param sessionId 电话号码+数字
     */
    suspend fun startQuestionFun(code:String,sessionId:String ): StartAnswerBean?{
        return  startQuestionDatasource.load(code,sessionId)
    }

    /**
     * 下一题以及上一题的答案
     *@param code  如 Q&A
     *@param currentNodeId  当前ip
     *@param sessionId
     *@param userResp  none/input/select/end
     */
    suspend fun startNextQuestion(code:String,
                                 currentNodeId:String,
                                 sessionId:String ,
                                 userResp:String): StartAnswerBean?{
        return  nextQuestionDatasource.load(code,currentNodeId,sessionId,userResp)
    }


}