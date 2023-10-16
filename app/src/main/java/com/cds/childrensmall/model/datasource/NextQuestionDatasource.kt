package com.cds.childrensmall.model.datasource

import android.content.Context
import com.cds.childrensmall.model.api.ApiService
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.bean.StartAnswerBean
import com.cds.childrensmall.util.net.RetrofitUtils
import com.cds.childrensmall.util.net.toJsonObjectForApi


/**
 * 开始问答
 */
class NextQuestionDatasource {

    /**
     * 下一题以及上一题的答案
     */
    suspend fun load (code:String,
                      currentNodeId:String,
                      sessionId:String ,
                      userResp:String
    ): StartAnswerBean? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .startNextQuestion(
                    code,
                    currentNodeId,
                    sessionId,
                    userResp
                )
                .getDataIfSuccess()
    }

}