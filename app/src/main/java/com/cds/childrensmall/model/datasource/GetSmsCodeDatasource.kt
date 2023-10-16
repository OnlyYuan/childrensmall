package com.cds.childrensmall.model.datasource

import android.content.Context
import com.cds.childrensmall.model.api.ApiService
import com.cds.childrensmall.util.net.RetrofitUtils
import com.cds.childrensmall.util.net.toJsonObjectForApi


/**
 * 或者短信验证码
 */
class GetSmsCodeDatasource {

    /**
     * 在线反馈
     * @param phone //电话号码
     */
    suspend fun load(phone: String): String? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .getSmsCode(
                     mapOf(
                        "phone" to phone,
                    ).toJsonObjectForApi()
                )
                .getDataIfSuccess()
    }

}