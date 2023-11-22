package com.cds.childrensmall.service

import android.util.Log
import com.cds.childrensmall.common.unitySmallToBigString
import org.greenrobot.eventbus.EventBus

/**
 * 被unity调用
 */
class UnityHelper {
    fun sendUnityMessage(msg:String){
        val mString = "$unitySmallToBigString?$msg"
        EventBus.getDefault().post(mString)
        Log.i("11","-->调用了UnityHelper sendUnityMessage方法${mString}")
        Log.i("11","-->msg.length${msg.length}")
    }
}