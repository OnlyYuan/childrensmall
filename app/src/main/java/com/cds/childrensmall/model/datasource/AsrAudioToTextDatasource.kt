package com.cds.childrensmall.model.datasource

import android.content.Context
import com.cds.childrensmall.model.api.ApiService
import com.cds.childrensmall.model.bean.AsrMessageDataBean
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.util.net.RetrofitUtils
import com.cds.childrensmall.util.net.toJsonObjectForApi
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * 获取全局数据
 */
class AsrAudioToTextDatasource {

    /**
     * 获取全局数据
     */
    suspend fun load(filePath:String,filename:String,sessionId:String): AsrMessageDataBean? {
          val file = File(filePath)
          val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),file)
          val filePart = MultipartBody.Part.createFormData("file",filename,requestBody)

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .asrFun(
                    sessionId,
                    filePart
                )
                .getDataIfSuccess()
    }

}