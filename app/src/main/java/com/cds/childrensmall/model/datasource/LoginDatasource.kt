package com.cds.childrensmall.model.datasource

import android.content.Context
import com.cds.childrensmall.model.api.ApiService
import com.cds.childrensmall.util.net.RetrofitUtils
import com.cds.childrensmall.util.net.toJsonObjectForApi


/**
 * 或者短信验证码
 */
class LoginDatasource {

    /**
     * 在线反馈
     * @param phone //电话号码
     * @param code //验证码
     * @param channel_id //渠道号
     */
    suspend fun load(phone: String,code:String,channel_id:Int): String? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .login(
                     mapOf(
                        "phone" to phone,
                        "code" to code,
                        "channel_id" to channel_id,
                    ).toJsonObjectForApi()
                )
                .getDataIfSuccess()
    }

}