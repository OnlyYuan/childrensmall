package com.cds.childrensmall.util

import android.content.Context
import android.os.Looper
import android.util.Log
import android.widget.Toast
import org.greenrobot.eventbus.EventBus
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MySocket(private val ip:String,private val port:Int):Thread(){
    lateinit var mSocket: Socket
    private  var mOutStream:OutputStream? = null
    private  var bufferedWriter:BufferedWriter?=null
    private var br :BufferedReader? = null
    private  val mThreadPool  = Executors.newCachedThreadPool()
    private  val mThreadPool2  = Executors.newCachedThreadPool()
    var isSuccess = true

    override fun run() {
        super.run()

        try {
            mSocket = Socket(ip,port)
            Log.i("11","-->链接成功")
            isSuccess = true
            EventBus.getDefault().post("socketOk")
            sendMessage("connect?${totalNickname}?${totalSessionId}")
            getMessage()
        }catch (iOException:IOException){
            Log.i("11","-->链接失败")
            isSuccess = false
            EventBus.getDefault().post("socketfail")
        }
    }

     fun sendMessage(msg:String){
         mThreadPool.execute {

             try {
                 mOutStream = mSocket.getOutputStream()
                 bufferedWriter = BufferedWriter(OutputStreamWriter(mOutStream))
                 Log.i("11","-->发送准备")
                 bufferedWriter!!.write(msg+"\n")
                 bufferedWriter!!.flush()
                 Log.i("11","-->发送成功${msg}")
             }catch (e:Exception){
                 isSuccess = false
                 EventBus.getDefault().post("socketfail")
                 Log.i("11","-->发送失败")
             }
         }
    }

    /**
     * 获取服务器信息
     */
    fun getMessage(){
        mThreadPool2.execute {
            br = BufferedReader(InputStreamReader(mSocket.getInputStream()))
            bufferedWriter = BufferedWriter(OutputStreamWriter(mSocket.getOutputStream()))
            try {
                Log.i("11","--->进入到读取到信息")
                var content :String?= ""
                while ((readFromClient().also { content = it })!="-1"){
                    if (content?.isNotEmpty() == true){
                        Log.i("11","--->读取到信息${content}")
                        EventBus.getDefault().post(content)
                    }
                }

            }catch (e:Exception){
                Log.i("11","--->printStackTrace${e.printStackTrace()}")
            }
        }
    }

    private fun readFromClient():String?{
        try {
            val  string = br?.readLine()
            //Log.i("222","--->读取readFromClient}")
            return  string
        }catch (e:Exception){
            Log.i("222","--->Exception${e.message.toString()}")
        }
        return "-1"
    }

}