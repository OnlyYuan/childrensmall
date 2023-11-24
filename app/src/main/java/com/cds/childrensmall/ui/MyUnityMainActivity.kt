package com.cds.childrensmall.ui

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.cds.childrensmall.R
import com.cds.childrensmall.common.unitySmallToBigString
import com.cds.childrensmall.common.widget.ImageDialog
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MyUnityMainActivity : UnityPlayerActivity() {

   // private lateinit var  imageView: ImageView
    private var isCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_my_unity_main)
    //    imageView = findViewById(R.id.image)
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        val msgArr = msg.split("?")

        when(msgArr[0]){
            unitySmallToBigString->{
                Log.i("11","small---->${msgArr[1].length}")
                if (isCount<1){
                    val qrcodeDialog = ImageDialog(this,msgArr[1])
                    qrcodeDialog.show()
                  isCount++
                }


                }

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}