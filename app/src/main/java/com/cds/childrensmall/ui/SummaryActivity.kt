package com.cds.childrensmall.ui

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.cds.childrensmall.R
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.common.summaryClose
import com.cds.childrensmall.common.summaryGoNext
import com.cds.childrensmall.databinding.ActivitySummaryBinding
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.util.totalAnswerScore
import com.cds.childrensmall.util.totalCurrentLevel
import com.cds.childrensmall.util.totalGameScore
import com.cds.childrensmall.util.totalReadScore
import org.greenrobot.eventbus.EventBus

/**
 * 总结页面
 */
class SummaryActivity : BaseActivity() {
    private var curContent: ConfigDataBean.ContentData?=null //配置数据
    private lateinit var mBinding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_summary)

        initData()
        initView()
    }

    private fun initData() {
        curContent = intent.getParcelableExtra("curContent") as ConfigDataBean.ContentData?
    }

    private fun initView() {
        mBinding.answerStar.text = getString(R.string.star_num_string, totalAnswerScore.toString())
        mBinding.readStar.text = getString(R.string.star_num_string, totalReadScore.toString())
        mBinding.gameStar.text = getString(R.string.star_num_string, totalGameScore.toString())
        val totalScore = totalAnswerScore+ totalGameScore+ totalReadScore
        mBinding.totalStar.text = getString(R.string.star_num_string,totalScore.toString())
        mBinding.goNext.setOnClickListener { //下一章节
            EventBus.getDefault().post(summaryGoNext)
            totalCurrentLevel = 0
            val intent = Intent()
            intent.putExtra("next",true)
            this@SummaryActivity.setResult(101,intent)
            this@SummaryActivity.finish()
        }
        mBinding.closeBtn.setOnClickListener {
            EventBus.getDefault().post(summaryClose)
            this@SummaryActivity.finish()
        }
    }


}