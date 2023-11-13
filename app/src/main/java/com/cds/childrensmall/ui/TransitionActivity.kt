package com.cds.childrensmall.ui

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.databinding.DataBindingUtil
import com.cds.childrensmall.R
import com.cds.childrensmall.databinding.ActivityTransitionBinding
import com.cds.childrensmall.model.bean.ConfigDataBean
import kotlin.math.roundToInt

/**
 * 过渡页
 */
class TransitionActivity : AppCompatActivity() {

    private var curProcess = 0 //当前进度
    private lateinit var mBinding:ActivityTransitionBinding
    private var curContent: ConfigDataBean.ContentData?=null //配置数据
    private var toActivity = ""//要到的activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_transition)
        initData()
    }

    private fun initData() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
        mBinding.btn.setOnClickListener {
            startProgressMove()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
     //   startProgressMove()
    }

    private  fun startProgressMove(){
//        val myCountIme = MyCounterTime(2000L,50L)
//        myCountIme.start()
        translateAnimation()
    }

    private fun translateAnimation(){
        val toX = mBinding.progress.width*1.0f -mBinding.img.width
//        Log.i("11","-->fromX${fromX}  toX${toX}  fromToY${fromToY}")
//        val translateAnimation = TranslateAnimation(0f,toX,0f,0f)
//        translateAnimation.duration = 2000
//        animationSet.addAnimation(translateAnimation)
//        mBinding.img.startAnimation(animationSet)

        val objectAnimator = ObjectAnimator.ofFloat(mBinding.img,"translationX",toX)
        objectAnimator.duration = 2000
        objectAnimator.addUpdateListener {
            curProcess += ((it.animatedValue) as Float).roundToInt()
            mBinding.progress.progress = curProcess
            Log.i("11","-->curProcess${curProcess}")
        }
        objectAnimator.start()
    }


    inner class MyCounterTime(var totalTime:Long,var countInterval:Long ):
        CountDownTimer(totalTime,countInterval){
        override fun onTick(millisUntilFinished: Long) {
            curProcess += countInterval.toInt()
            mBinding.progress.progress = curProcess
        }

        override fun onFinish() {
            mBinding.progress.progress = 2000
            startFun()
        }
    }

    /**
     * 跳转页面
     */
    private fun startFun() {
        when(toActivity){
                "StoryActivity"->{

                }


        }
    }

}