package com.cds.childrensmall.model.repository

import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.datasource.GetConfigDatasource
import com.cds.childrensmall.model.datasource.GetSmsCodeDatasource
import com.cds.childrensmall.model.datasource.LoginDatasource


/**
 * 全局数据数据仓库
 */
class DataRepository(
    private val getConfigDatasource: GetConfigDatasource
) {


    /**
     * 获取全局数据
     *
     */
    suspend fun getConfigData(): ConfigDataBean?{
        return  getConfigDatasource.load()
    }

}