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
class StartQuestionDatasource {

    /**
     * 开始答题准备
     */
    suspend fun load (code:String,sessionId:String ): StartAnswerBean? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .startAnswer(
                    code,
                    sessionId
                )
                .getDataIfSuccess()
    }

}