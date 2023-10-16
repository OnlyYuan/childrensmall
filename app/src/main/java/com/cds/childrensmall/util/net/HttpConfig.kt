package com.raisingai.jubenyu.utils.net

class HttpConfig(
    var connectTimeOut: Long = 10,
    var readTimeout: Long = 10,
    var writeTimeout: Long = 10,
    var baseUrl:String = ""
)