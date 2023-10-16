package com.cds.childrensmall.model.datasource

import com.cds.childrensmall.model.api.ApiService
import com.cds.childrensmall.model.bean.AsrMessageDataBean
import com.cds.childrensmall.util.net.RetrofitUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


/**
 * 获取全局数据
 */
class GetReadScoreDatasource {

    /**
     * 获取全局数据
     */
    suspend fun load(filePath:String,filename:String,genDuId:String,sessionId:String): Int? {
          val file = File(filePath)
          val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(),file)
          val filePart = MultipartBody.Part.createFormData("file",filename,requestBody)

          return  RetrofitUtils
                .getInstance()
                .getService<ApiService>()
                .getReadSoreFun(
                    genDuId,
                    sessionId,
                    filePart
                )
                .getDataIfSuccess()
    }

}