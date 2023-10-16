package com.cds.childrensmall.model.repository

import com.cds.childrensmall.model.datasource.GetSmsCodeDatasource
import com.cds.childrensmall.model.datasource.LoginDatasource


/**
 * 登录数据仓库
 */
class LoginRepository(
    private val getSmsCodeDatasource: GetSmsCodeDatasource,
    private val loginDatasource: LoginDatasource
) {

    /**
     * 获取验证码
     *
     */
    suspend fun getSmsCode(phone: String): String?{
      return  getSmsCodeDatasource.load(phone)
    }


    /**
     * 获取验证码
     *
     */
    suspend fun login(phone: String,code:String,channel_id:Int): String?{
        return  loginDatasource.load(phone,code,channel_id)
    }

}