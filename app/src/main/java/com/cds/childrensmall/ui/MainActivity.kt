package com.cds.childrensmall.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.cds.childrensmall.R
import com.cds.childrensmall.databinding.ActivityMainBinding
import com.cds.childrensmall.base.BaseActivity
import com.cds.childrensmall.common.mainToucSummaryBtn
import com.cds.childrensmall.common.mainTouchAnswerBtn
import com.cds.childrensmall.common.mainTouchReadBtn
import com.cds.childrensmall.common.mainTouchStoryBtn
import com.cds.childrensmall.model.bean.ConfigDataBean
import com.cds.childrensmall.service.ClientSocketService
import com.cds.childrensmall.util.SharedPreferencesUtils
import com.cds.childrensmall.util.net.DataHandler
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
    }

    override fun onClick(v: View?) {
       when(v){
           mBinding.storyBtn->{
               if (curPosition!=-1){
                   val intent = Intent(this@MainActivity,StoryActivity::class.java)
                   intent.putExtra("curContent",contentDataList[curPosition])
                   startActivity(intent)
                   EventBus.getDefault().post(mainTouchStoryBtn)
               }

           }
           mBinding.redBtn->{
               if (curPosition!=-1){
                   val intent = Intent(this@MainActivity,ReadActivity::class.java)
                   intent.putExtra("curContent",contentDataList[curPosition])
                   startActivity(intent)
                   EventBus.getDefault().post(mainTouchReadBtn)
               }
           }

           mBinding.answerBtn->{
               if (curPosition!=-1) {
                   val intent = Intent(this@MainActivity, QuestionActivity::class.java)
                   intent.putExtra("curContent", contentDataList[curPosition])
                   startActivity(intent)
                   EventBus.getDefault().post(mainTouchAnswerBtn)
               }
           }
           mBinding.sumBtn->{
               if (curPosition!=-1) {
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
               changeTagFun(-1)
           }
           mBinding.next->{//下一个
               changeTagFun(1)
           }
       }

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
            }

            "socketOk"->{//连接成功
                Toast.makeText(this,"和屏幕连接成功！",Toast.LENGTH_SHORT).show()
            }
        }

    }
}