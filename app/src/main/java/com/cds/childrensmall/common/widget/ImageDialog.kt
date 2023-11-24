package com.cds.childrensmall.common.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.cds.childrensmall.R
import com.cds.childrensmall.databinding.DialogQrcodeBinding
import com.cds.childrensmall.util.dp
import android.util.Base64
import android.view.Window
import android.widget.ImageView


/**
 * 原因弹窗
 * @auth cpf
 * @date 2022/7/26
 */
class ImageDialog(
    private var context: Context,
    private var ipString: String
): Dialog(context) {
    private lateinit var  imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     //   mBinding= DataBindingUtil.inflate(inflater, R.layout.dialog_qrcode,container,false)
       setContentView(R.layout.dialog_qrcode)
        imageView = findViewById(R.id.qrCode)
        initView()
    }

    private fun initView() {
        window?.setLayout(400,400)
        window?.setGravity(Gravity.CENTER)
        val decodeString = Base64.decode(ipString, Base64.DEFAULT)
        val bitMap = BitmapFactory.decodeByteArray(decodeString,0,decodeString.size)
        imageView.setImageBitmap(bitMap)

    }



}