package com.cds.childrensmall.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cds.childrensmall.R
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.databinding.ActivitySplashBinding
import com.cds.childrensmall.model.datasource.LoginDatasource
import com.cds.childrensmall.util.SharedPreferencesUtils

/**
 * @param cpf
 */
class MySplashActivity : BaseActivity() {

    private lateinit var  mBinding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_splash)
        initVideo()
        initLisenter()
    }

    private fun initLisenter() {
        mBinding.goLogin.setOnClickListener {
            goFun()
        }
    }

    private fun initVideo() {
        mBinding.videoView.setVideoURI (Uri.parse("android.resource://" + packageName + "/"+ R.raw.splash))
        mBinding.videoView.start()
        mBinding.videoView.setOnPreparedListener{

        }
    }

    private fun goFun() {
        if ( SharedPreferencesUtils.getValue(this,
                SharedPreferencesUtils.UserInfo,
                SharedPreferencesUtils.ISLOGIN,
                "0")=="1"){
            startActivity(Intent(this@MySplashActivity,MainActivity::class.java))
            this@MySplashActivity.finish()
        }else{
            startActivity(Intent(this@MySplashActivity,LoginActivity::class.java))
            this@MySplashActivity.finish()
        }
    }
}