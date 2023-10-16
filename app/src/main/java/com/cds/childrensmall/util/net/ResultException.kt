package com.cds.childrensmall.util.net

data class ResultException(
    val code:Int,
    val msg:String
) :RuntimeException(msg)