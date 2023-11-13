package com.cds.childrensmall.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cds.childrensmall.R
import com.cds.childrensmall.databinding.ActivityMainBinding
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.common.mainToucSummaryBtn
import com.cds.childrensmall.common.mainTouchAnswerBtn
import com.cds.childrensmall.common.mainTouchGameBtn
import com.cds.childrensmall.common.mainTouchReadBtn
import com.cds.childrensmall.common.mainTouchStoryBtn
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.service.ClientSocketService
import com.cds.childrensmall.util.SharedPreferencesUtils
import com.cds.childrensmall.util.net.DataHandler
import com.cds.childrensmall.util.totalCurrentLevel
import com.cds.childrensmall.util.totalNickname
import com.cds.childrensmall.util.totalSessionId
import com.cds.childrensmall.viewmodel.MainViewModel
import com.google.zxing.client.android.Intents
import com.journeyapps.barcodescanner.CaptureActivity
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.random.Random

/**
 *
 */

class MainActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mBinding:ActivityMainBinding
    private val mViewModel: MainViewModel by viewModel()
    private val contentDataList = ArrayList<ConfigDataBean.ContentData>()//关卡list
    private var totalCount  = 0 //总关卡
    private var curPosition = -1//关卡位置
    private var isConnection = false  //是否连接
    private var rotateAnimation:ObjectAnimator?=null //光圈的旋转动画
    private var lastView:View? = null //上一个动画view

    private val summaryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (
            it.data?.getBooleanExtra("next",false) == true
        ){
            changeTagFun(1)
            Log.i("11","-->next")
        }else{
            Log.i("11","-->返回")
        }
    }

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            val mString = it.data?.getStringExtra(Intents.Scan.RESULT)
            if (!mString.isNullOrEmpty()){
                 saveData(mString)
                 startSocketFun(mString)
            }else{
                Toast.makeText(this@MainActivity,"扫描失败",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
       if (it){
           setPermissionFun()
       }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        EventBus.getDefault().register(this)
        initView()
        initListener()
        //initSocket()
        getConfigDataFun()
    }

    private fun initListener() {
       mBinding.storyBtn.setOnClickListener(this)
       mBinding.redBtn.setOnClickListener(this)
       mBinding.answerBtn.setOnClickListener(this)
       mBinding.sumBtn.setOnClickListener(this)
       mBinding.connect.setOnClickListener(this)
       mBinding.backBtn.setOnClickListener(this)
       mBinding.next.setOnClickListener(this)
    }

    private fun initView() {

       val phone = SharedPreferencesUtils.getValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.PHONE,
            "")?:""
        val num = Random.nextInt(100)
        totalSessionId = "$phone$num"
        Log.i("11","-->totalSessionId${totalSessionId}")
        val ipString = SharedPreferencesUtils.getValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.IP,
            "")?:""
        if ( ipString!=""){
            startSocketFun(ipString)
        }
        totalNickname = (SharedPreferencesUtils.getValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.NICK_NAME,
            ""))?:""
        mBinding.nickName.text = totalNickname
        Glide.with(this@MainActivity)
            .load(R.mipmap.bear_wave)
            .into(mBinding.bearGif)
    }

    override fun onResume() {
        super.onResume()
      //  showDongtu()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        showDongtu()
    }

    override fun onClick(v: View?) {
       when(v){
           mBinding.storyBtn->{
               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,"先扫描链接大屏幕",Toast.LENGTH_SHORT).show()
                   return
               }

               if (curPosition!=-1){
                   totalCurrentLevel=0
                   val intent = Intent(this@MainActivity,StoryActivity::class.java)
                   intent.putExtra("curContent",contentDataList[curPosition])
                   startActivity(intent)
                   EventBus.getDefault().post(mainTouchStoryBtn)
               }

           }
           mBinding.redBtn->{

               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,getString(R.string.connect_tips),Toast.LENGTH_SHORT).show()
                   return
               }

               if (curPosition!=-1){
                   totalCurrentLevel =1
                   val intent = Intent(this@MainActivity,ReadActivity::class.java)
                   intent.putExtra("curContent",contentDataList[curPosition])
                   startActivity(intent)
                   EventBus.getDefault().post(mainTouchReadBtn)
               }
           }

           mBinding.answerBtn->{
               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,getString(R.string.connect_tips),Toast.LENGTH_SHORT).show()
                   return
               }

               if (curPosition!=-1) {
                   totalCurrentLevel =2
                   val intent = Intent(this@MainActivity, QuestionActivity::class.java)
                   intent.putExtra("curContent", contentDataList[curPosition])
                   startActivity(intent)
                   EventBus.getDefault().post(mainTouchAnswerBtn)
               }
           }

           mBinding.gameBtn->{
               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,getString(R.string.connect_tips),Toast.LENGTH_SHORT).show()
                   return
               }

               if (curPosition!=-1) {
                    totalCurrentLevel =3
                   EventBus.getDefault().post(mainTouchGameBtn)
               }
           }


           mBinding.sumBtn->{

               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,getString(R.string.connect_tips),Toast.LENGTH_SHORT).show()
                   return
               }

               if (curPosition!=-1) {
                   totalCurrentLevel =4
                   val intent = Intent(this@MainActivity, SummaryActivity::class.java)
                   intent.putExtra("curContent", contentDataList[curPosition])
                   summaryLauncher.launch(intent)
                   EventBus.getDefault().post(mainToucSummaryBtn)
               }
           }

           mBinding.connect->{//扫描链接
               setPermissionFun()
           }
           mBinding.backBtn->{//上一个
               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,getString(R.string.connect_tips),Toast.LENGTH_SHORT).show()
                   return
               }

               changeTagFun(-1)
           }
           mBinding.next->{//下一个

               if (!isConnection){//不让点击
                   Toast.makeText(this@MainActivity,getString(R.string.connect_tips),Toast.LENGTH_SHORT).show()
                   return
               }

               changeTagFun(1)
           }
       }

    }


    /**
     * 锁定下一个位置的动图
     */
    private fun showDongtu(){
        stopAnimationFun()
        //setImgFun()
        when(totalCurrentLevel){
            0->{
               mBinding.lightRing.x = mBinding.storyBtn.x -(mBinding.lightRing.width-mBinding.storyBtn.width)/2
               mBinding.lightRing.y = mBinding.storyBtn.y -(mBinding.lightRing.height-mBinding.storyBtn.height)/2
               bigSmallAnimationFun(mBinding.storyBtn)
                lastView = mBinding.storyBtn
            }

            1->{
                mBinding.lightRing.x = mBinding.redBtn.x -(mBinding.lightRing.width-mBinding.redBtn.width)/2
                mBinding.lightRing.y = mBinding.redBtn.y -(mBinding.lightRing.height-mBinding.redBtn.height)/2
                bigSmallAnimationFun(mBinding.redBtn)
                lastView = mBinding.redBtn
            }

            2->{
                mBinding.lightRing.x = mBinding.answerBtn.x -(mBinding.lightRing.width-mBinding.answerBtn.width)/2
                mBinding.lightRing.y = mBinding.answerBtn.y -(mBinding.lightRing.height-mBinding.answerBtn.height)/2
                bigSmallAnimationFun(mBinding.answerBtn)
                lastView = mBinding.answerBtn
            }

            3->{
                mBinding.lightRing.x = mBinding.gameBtn.x -(mBinding.lightRing.width-mBinding.gameBtn.width)/2
                mBinding.lightRing.y = mBinding.gameBtn.y -(mBinding.lightRing.height-mBinding.gameBtn.height)/2
                bigSmallAnimationFun(mBinding.gameBtn)
                lastView = mBinding.gameBtn
            }

            4->{

                mBinding.lightRing.x = mBinding.sumBtn.x -(mBinding.lightRing.width-mBinding.sumBtn.width)/2
                mBinding.lightRing.y = mBinding.sumBtn.y -(mBinding.lightRing.height-mBinding.sumBtn.height)/2
                bigSmallAnimationFun(mBinding.sumBtn)
                lastView = mBinding.sumBtn
            }
        }
        lightRingAnimationFun()
    }

    /**
     * 光圈转动
     */
    private fun lightRingAnimationFun(){

        rotateAnimation = ObjectAnimator.ofFloat( mBinding.lightRing,"rotation",0f,359f)
        rotateAnimation?.duration = 1500
        rotateAnimation?.repeatCount = ValueAnimator.INFINITE
        rotateAnimation?.interpolator = LinearInterpolator()
        rotateAnimation?.start()

    }

    /**
     * view 放大缩小动画
     */
    private fun bigSmallAnimationFun(view:View){
        val scaleAnimation = ScaleAnimation(1.0f,1.1f,1.0f,1.1f,
            Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        scaleAnimation.duration  =300
        scaleAnimation.repeatCount = ScaleAnimation.INFINITE
        scaleAnimation.repeatMode = Animation.REVERSE
        view.startAnimation(scaleAnimation)
    }

    /**
     * 停止动画
     */
    private fun stopAnimationFun(){
        rotateAnimation?.cancel()
        lastView?.clearAnimation()
    }


    private fun setPermissionFun(){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED
            ){
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        scanQRCode()
    }

    /**
     * 调用二维码扫描
     */
    private fun scanQRCode(){
        activityLauncher.launch(Intent(this@MainActivity,CaptureActivity::class.java))
    }

    /**
     * 获取数据信息
     */
    private fun getConfigDataFun(){

        lifecycleScope.launch {
            DataHandler.performCollect(
                this@MainActivity,
                block = {
                    mViewModel.getConfigData()
                },
                onError = {
                    Log.i("11","--->失败")

                },
                onSuccess = {
                     it?.contentList?.let { it1->
                         contentDataList.addAll(it1)
                         totalCount = it1.size
                         curPosition = 0
                         showTagUi()
                     }
                }
            )
        }
    }

    /**
     * 存储登录数据
     */
    private fun saveData(ipString:String){
        SharedPreferencesUtils.saveValue(this,
            SharedPreferencesUtils.UserInfo,
            SharedPreferencesUtils.IP,
           ipString)

    }

    /**
     * 启动socket
     */
    private fun startSocketFun(ipString:String){
        val intent = Intent(this@MainActivity,ClientSocketService::class.java)
        intent.putExtra("ip",ipString)
        startService(intent)
    }

    /**
     * 切换标签
     * -1 上一个   1 下一个
     */
    private fun changeTagFun(position:Int){
        if (position+curPosition<0||position+curPosition>=totalCount){
            Toast.makeText(this@MainActivity,"没有更多了！",Toast.LENGTH_SHORT).show()
        }else{
            totalCurrentLevel = 0
            curPosition += position
            showTagUi()
        }
    }

    /**
     * 展示标签ui
     */
    private fun showTagUi(){
        mBinding.contentName.text = contentDataList[curPosition].name ?: ""
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMsg(msg: String){
        when(msg){
            "socketfail"->{//连接失败
                Toast.makeText(this,"和屏幕连接失败！",Toast.LENGTH_SHORT).show()
                isConnection =false
            }

            "socketOk"->{//连接成功
                isConnection = true
                Toast.makeText(this,"和屏幕连接成功！",Toast.LENGTH_SHORT).show()
            }
        }

    }
}