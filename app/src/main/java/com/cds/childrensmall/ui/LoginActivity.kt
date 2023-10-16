package com.cds.childrensmall.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cds.childrensmall.R
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.databinding.ActivityLoginBinding
import com.cds.childrensmall.util.SharedPreferencesUtils
import com.cds.childrensmall.util.net.DataHandler
import com.cds.childrensmall.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 登录页面
 */
class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding:ActivityLoginBinding
    private val mViewModel:LoginViewModel by viewModel()
    private var isCan = true
    private var countTime = 30 //倒计时

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)

        initView()
        initListener()
    }

    private fun initListener() {
        mBinding.codeBtn.setOnClickListener(this)
        mBinding.cancelBtn.setOnClickListener(this)
        mBinding.confirmBtn.setOnClickListener(this)
    }

    private fun initView() {
            if ( SharedPreferencesUtils.getValue(this,
                    SharedPreferencesUtils.UserInfo,
                    SharedPreferencesUtils.ISLOGIN,
                    "0")=="1"){
                startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                this@LoginActivity.finish()
            }
    }

    override fun onClick(v: View?) {
       when(v){
           mBinding.codeBtn->{//短信验证

               if (isCan){
                   if (mBinding.phoneNum.text.length !=11){
                       Toast.makeText(this,"请输入正确手机号！",Toast.LENGTH_SHORT).show()
                       return
                   }
                   countTime = 30
                   val phone = mBinding.phoneNum.text.toString()
                   getSmsCode(phone)
                   MyCounterTime(30000,1000).start()
                   isCan =false
                   mBinding.codeBtn.text = countTime.toString()
               }
           }

           mBinding.cancelBtn->{

           }

           mBinding.confirmBtn->{

               val nickName =  mBinding.nickName.text.toString()
               val phone = mBinding.phoneNum.text.toString()
               val code = mBinding.code.text.toString()
               if (nickName.isNullOrEmpty()){
                   Toast.makeText(this,"请输入昵称！",Toast.LENGTH_SHORT).show()
                   return
               }else if (phone.isNullOrEmpty()){
                   Toast.makeText(this,"请输入手机号！",Toast.LENGTH_SHORT).show()
                   return
               }else if (code.isNullOrEmpty()){
                   Toast.makeText(this,"请输入验证码！",Toast.LENGTH_SHORT).show()
                   return
               }
               loginFun(phone,code)
           }
       }
    }

    /**
     * 或者短信认证
     */
    private fun getSmsCode(phone:String){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@LoginActivity,
                block = {
                    mViewModel.getSmsCode(phone)
                },
                onError = {
                    Log.i("11","--->失败")
                },
                onSuccess = {

                    Log.i("11","--->成功${it}")
                }
            )
        }
    }


    /**
     * 登录
     */
    private fun loginFun(phone:String,code:String){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@LoginActivity,
                block = {
                    mViewModel.login(phone,code,1)
                },
                onError = {
                    Log.i("11","--->失败")
                    Toast.makeText(this@LoginActivity,"验证码失效",Toast.LENGTH_SHORT).show()
                },
                onSuccess = {
                    saveData()
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    this@LoginActivity.finish()
                    Log.i("11","--->成功${it}")
                }
            )
        }
    }

    /**
     * 存储登录数据
     */
    private fun saveData(){
        SharedPreferencesUtils.saveValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.NICK_NAME,
            mBinding.nickName.text.toString())

        SharedPreferencesUtils.saveValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.PHONE,
            mBinding.phoneNum.text.toString())

        SharedPreferencesUtils.saveValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.ISLOGIN,
           "1")
    }

    inner class MyCounterTime(var totalTime:Long,var countInterval:Long ):
        CountDownTimer(totalTime,countInterval){
        override fun onTick(millisUntilFinished: Long) {
            countTime -= 1
            mBinding.codeBtn.text = countTime.toString()
        }

        override fun onFinish() {
            isCan = true
            mBinding.codeBtn.text = "发送验证码"
        }

    }

}