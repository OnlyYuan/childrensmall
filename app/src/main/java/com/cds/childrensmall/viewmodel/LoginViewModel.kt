package com.cds.childrensmall.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.cds.childrensmall.model.repository.DataRepository
import com.cds.childrensmall.model.repository.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val dataRepository: DataRepository
) : ViewModel() {

    /**
     * 获取用户信息
     */
    suspend fun getSmsCode(phone: String): String?{
        return loginRepository.getSmsCode(phone)
    }


    /**
     * 获取验证码
     *
     */
    suspend fun login(phone: String,code:String,channel_id:Int): String?{
        return  loginRepository.login(phone,code,channel_id)
    }

}