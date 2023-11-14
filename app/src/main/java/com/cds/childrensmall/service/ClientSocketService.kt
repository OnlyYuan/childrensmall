package com.cds.childrensmall.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.cds.childrensmall.common.mainTouchStoryBtn
import com.cds.childrensmall.common.readNextVoiceBtn
import com.cds.childrensmall.common.readStartReadBtn
import com.cds.childrensmall.common.unitySmallToBigString
import com.cds.childrensmall.util.MySocket
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ClientSocketService : Service() {

    private lateinit var mySocket:MySocket

    override fun onCreate() {
        EventBus.getDefault().register(this)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val ip = intent?.getStringExtra("ip")?:""
        initSocket(ip)
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 启动socket
      */
    private fun initSocket(ipString: String) {
        try {
            mySocket = MySocket(ipString,8080)
            mySocket.start()
        }catch (e:Exception){
            Log.i("11","-===>e ${e.message}")
        }

    }

    /**
     * 发送消息
     */
    private fun sendMessage(msg:String){
        if (mySocket.isSuccess){
            mySocket.sendMessage(msg)
        }
    }

    /**
     * 发送unity信息
     */
    private fun sendUnityMessage(msg:String){
        val mString = "${unitySmallToBigString}?$msg"
        if (mySocket.isSuccess){
            mySocket.sendMessage(msg)
        }
        Log.i("11","-->调用了sendUnityMessage方法")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("11","-->服务关闭！！")
        EventBus.getDefault().unregister(this)
    }

    /**
     * 接收点击信息 发送给服务端
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        //Log.i("11","--->service ${msg}")
        when(msg){
            readStartReadBtn, readNextVoiceBtn ->{//服务器返回的跟读

            }
            else->{
                sendMessage(msg)
            }
        }

    }


}