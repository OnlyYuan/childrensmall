package com.cds.childrensmall.model.datasource

import android.content.Context
import com.cds.childrensmall.model.api.ApiService
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.util.net.RetrofitUtils
import com.cds.childrensmall.util.net.toJsonObjectForApi


/**
 * 获取全局数据
 */
class GetConfigDatasource {

    /**
     * 获取全局数据
     */
    suspend fun load(): ConfigDataBean? {

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .getConfigData()
                .getDataIfSuccess()
    }

}