package com.cds.childrensmall.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.repository.DataRepository
import com.cds.childrensmall.model.repository.LoginRepository

class MainViewModel(

    private val dataRepository: DataRepository
) : ViewModel() {

    /**
     * 获取配置信息用户信息
     */
    suspend fun getConfigData():  ConfigDataBean?{
        return dataRepository.getConfigData()
    }


}