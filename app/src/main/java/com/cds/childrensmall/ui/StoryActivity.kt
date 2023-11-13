package com.cds.childrensmall.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.cds.childrensmall.R
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.common.mainTouchAnswerBtn
import com.cds.childrensmall.common.storyBackBtn
import com.cds.childrensmall.common.storyNextBtn
import com.cds.childrensmall.common.storyPlayBtn
import com.cds.childrensmall.databinding.ActivityStoryBinding
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.util.totalCurrentLevel
import org.greenrobot.eventbus.EventBus

/**
 * 读故事
 */
class StoryActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding:ActivityStoryBinding
    private var curContent: ConfigDataBean.ContentData?=null //配置数据

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_story)
        initData()
        initListener()
    }

    private fun initData() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
    }

    private fun initListener() {
        mBinding.backBtn.setOnClickListener(this)
        mBinding.nextBtn.setOnClickListener(this)
        mBinding.playBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
       when(v){
           mBinding.backBtn->{
               this@StoryActivity.finish()
               EventBus.getDefault().post(storyBackBtn)
           }

           mBinding.nextBtn->{
               EventBus.getDefault().post(storyNextBtn)
               val intent = Intent(this@StoryActivity,ReadActivity::class.java)
               intent.putExtra("curContent",curContent)
               startActivity(intent)
               totalCurrentLevel += totalCurrentLevel
               this@StoryActivity.finish()
           }

           mBinding.playBtn->{
               EventBus.getDefault().post(storyPlayBtn)
           }

       }
    }


}