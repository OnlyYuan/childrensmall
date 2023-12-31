package com.cds.childrensmall.ui



import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cds.childrensmall.R
import com.cds.childrensmall.adapter.AnswerAdapter
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.common.questionSelectAnswer
import com.cds.childrensmall.common.questiongoBack
import com.cds.childrensmall.databinding.ActivityQuestionBinding
import com.cds.childrensmall.model.bean.StartAnswerBean
import com.cds.childrensmall.util.net.DataHandler
import com.cds.childrensmall.util.totalAnswerScore
import com.cds.childrensmall.util.totalSessionId
import com.cds.childrensmall.viewmodel.QuestionViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * 问题页面
 */
class QuestionActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityQuestionBinding
    private lateinit var answerAdapter: AnswerAdapter
    private val questionNodeList = ArrayList<StartAnswerBean.NodeBean>() //答案list
    private lateinit var currentNode: StartAnswerBean.NodeBean //当前节点
    private var myCountIme:WaitCounterTime?=null  //完成后定时跳转
    private var myCountIme1:WaitCounterTime?=null  //答对后的等待
    private var isCanTouch = true //是否可以點擊 防止多次點擊答案

    val mViewModel :QuestionViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_question)
        totalAnswerScore = 0
        initListener()
        initRecycler()
        initView()
        startQuestionFun("Q&A")
    }

    private fun initView() {
        Glide.with(this@QuestionActivity)
            .load(R.mipmap.bear_jump)
            .into(mBinding.bear)
    }


    private fun initListener() {
        mBinding.backBtn.setOnClickListener(this)
    }


    private fun initRecycler() {
        answerAdapter = AnswerAdapter()
        val layoutManager = GridLayoutManager(this,2)
        mBinding.answerRecycle.layoutManager = layoutManager
        mBinding.answerRecycle.adapter = answerAdapter

        answerAdapter.setOnItemClickListener { adapter, view, position ->
            //点击选项后发到大屏幕
            if (isCanTouch){
                isCanTouch = false
                EventBus.getDefault().post("${questionSelectAnswer}?$position")
                startNextQuestion(currentNode.nodeId?:"",questionNodeList[position].nodeId)
            }
        }

    }

    override fun onClick(v: View?) {
       when(v){
           mBinding.backBtn->{//返回
               EventBus.getDefault().post(questiongoBack)
                this@QuestionActivity.finish()
           }

       }
    }

    /**
     * 开始答题准备
     *@param code  如 Q&A
     *@param sessionId 电话号码+数字
     */
    private fun startQuestionFun(code:String){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@QuestionActivity,
                block = {
                    mViewModel.startQuestionFun(code,totalSessionId)
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    it?.let {it1->
                        startNextQuestion(it1.current?.nodeId?:"")
                    }
                }
            )
        }
    }


    /**
     * 下一题以及上一题的答案
     *@param code  如 Q&A
     *@param currentNodeId  当前ip
     *@param sessionId
     *@param userResp  none/input/select/end
     */
    private fun startNextQuestion(currentNodeId:String,userResp:String?="none"){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@QuestionActivity,
                block = {
                    mViewModel.startNextQuestion("Q&A",currentNodeId,totalSessionId,userResp?:"none")
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                    it?.let {it1->
                        currentNode = it1.current!!
                        questionNodeList.clear()
                        it1.children?.let { it2 ->
                            mBinding.showStarView.visibility = View.GONE
                            questionNodeList.addAll(it2)

                        }
                        answerAdapter.setList(questionNodeList)
                    }
                    isCanTouch = true
                    if (it != null) {
                        when(it.current?.userRespWay){
                            "none"->{ //回答正确的时候
                                isSuccess = true
                                myCountIme1 = WaitCounterTime(it.current?.nodeId?:"",5000L,1000L)
                                myCountIme1?.start()
                                mBinding.showStarView.visibility = View.VISIBLE

                                if(it.current!!.content == "Well done!"){
                                    mBinding.scoreImg.setImageResource(R.mipmap.well_done)
                                }else{
                                    mBinding.scoreImg.setImageResource(R.mipmap.good_job)
                                }

                                totalAnswerScore+=3
                            }
                            "end"->{//结束
                                answerAdapter.setList(questionNodeList)
                                mBinding.tip.visibility = View.VISIBLE
                                totalAnswerScore+=3
                                myCountIme = WaitCounterTime("",7000L,1000L)
                                myCountIme?.start()
                               // Toast.makeText(this@QuestionActivity,"答题结束",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )
        }
    }

    private var isSuccess = false //讀正確
    inner class WaitCounterTime(var nodeId:String,var totalTime:Long,var countInterval:Long ):
        CountDownTimer(totalTime,countInterval){
        override fun onTick(millisUntilFinished: Long) {

        }

        override fun onFinish() {

            if (isSuccess){//成功后，等2s播放下一個
                startNextQuestion(nodeId)
                isSuccess = false
            }else{
                startActivity(Intent(this@QuestionActivity,MainActivity::class.java))
            }

        }

    }


    override fun onDestroy() {
        super.onDestroy()
        myCountIme?.cancel()
        myCountIme = null
        myCountIme1?.cancel()
        myCountIme1 = null
    }
}