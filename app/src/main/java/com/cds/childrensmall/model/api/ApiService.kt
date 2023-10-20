package com.cds.childrensmall.model.api

import com.cds.childrensmall.model.bean.AsrMessageDataBean
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.model.bean.StartAnswerBean
import com.cds.childrensmall.util.net.ResultModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {


    //获取登录验证码
    @POST("https://vrbt.service.aidcstore.net/login/getSmsCode")
    suspend fun getSmsCode(@Body param: JsonObject?): ResultModel<String?>


    //登录验证，获取token接口
    @POST("https://vrbt.service.aidcstore.net/login/smsLogin")
    suspend fun login(@Body param: JsonObject?): ResultModel<String?>

    //获取全局数据
    @GET("/config")
    suspend fun getConfigData():ResultModel<ConfigDataBean?>


    @Multipart
    @POST("/imApi/free/voiceToText")
    suspend fun asrFun(
        @Query("sessionId") sessionId:String,
        @Part file : MultipartBody.Part
    ):ResultModel<AsrMessageDataBean?>

    /**
     * 获取跟读评分
     */
    @Multipart
    @POST("/imApi/process/getGenDuScore")
    suspend fun getReadSoreFun(
        @Query("genDuId") genDuId:String,
        @Query("sessionId") sessionId:String,
        @Part file : MultipartBody.Part,
    ):ResultModel<Int?>

    /**
     * 开始答题准备
     */
    @GET("/imApi/process/startChat")
    suspend fun startAnswer(
        @Query("code") code:String,// Q&A
        @Query("sessionId") sessionId:String, //电话号码 + 随机
    ):ResultModel<StartAnswerBean>


    /**
     * 下一个节点（下一题和上一题的回答）
     */
    @GET("/imApi/process/getChatContent")
    suspend fun startNextQuestion(
        @Query("code") code:String,// Q&A
        @Query("currentNodeId") currentNodeId:String,//当前节点id
        @Query("sessionId") sessionId:String, //电话号码 + 随机
        @Query("userResp") userResp:String, //对话内容
    ):ResultModel<StartAnswerBean>


}