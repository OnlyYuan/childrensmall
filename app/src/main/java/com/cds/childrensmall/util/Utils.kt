package com.cds.childrensmall.util

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.inputmethod.InputMethodManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern


fun getMD5(string: String): String {
    var mbyte = string.toByteArray()
    var md5 = ""
    var sbString = StringBuffer()

    var messageDigest = MessageDigest.getInstance("MD5")
    messageDigest.update(mbyte)
    var temp = messageDigest.digest()

    for (b in temp) {
        //  sbString.append(Integer.toHexString(temp[i] & 0xff))
        sbString.append(Integer.toHexString(b.toInt() and 0xff))
    }
    md5 = sbString.toString()
    return md5
}

/**
 * 获取assert中的json资源
 */
fun getJson(fileName:String,context: Context):String{
    var stringBuffer = StringBuffer()

    try {
        var assetManager:AssetManager = context.assets
        var bf= BufferedReader(InputStreamReader(assetManager.open(fileName)))
        var line :String =""
        while ( bf.readLine()!=null){
            line = bf.readLine()
            stringBuffer.append(line)
        }
    }catch (e:IOException){

    }

    return stringBuffer.toString()
}


/**
 * 设置文字可点击和颜色部分
 * @param context
 * @param headString 前端字符串
 * @param contentString 点击部分内容
 * @param endString 结尾部分
 * @param textClick 点击
 *
 */
fun<T> setTextSpanPart(headString: String,contentString:String,endString: String,textClick: T):SpannableStringBuilder{
    val style = SpannableStringBuilder()
    style.append(headString).append(contentString)
    endString?.let {
        style.append(it)
    }

    style.setSpan(textClick, headString.length, headString.length+contentString.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return  style
}


/**
 * 正常编码中一般只会用到 [dp]/[sp] ;
 * 其中[dp]/[sp] 会根据系统分辨率将输入的dp/sp值转换为对应的px
 */
val Float.dp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

val Int.dp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()


val Float.sp: Float                 // [xxhdpi](360 -> 1080)
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)


val Int.sp: Int
    get() = android.util.TypedValue.applyDimension(
        android.util.TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()



/**
 * 将数字转换为中文（ 仅包含1-9）
 * @param count  输入的数字
 */
fun changeNumToHanzi( count :Int):String{

    val mString = arrayOf("一","二","三","四","五","六","七","八","九")

    return mString[count-1]

}

fun String.isPhoneNum():Boolean{
    val pattern = Pattern.compile("[0-9]*")
    val isNum = pattern.matcher(this)

    return this.length >5 && isNum.matches()
}

/**
 * 获取json并转换
 */
fun getJson( context: Context):String?{
    val stringBuilder = StringBuilder()
    try {
        val bf = BufferedReader(context.resources.assets.open("yuanri.json").bufferedReader())
        var line:String? =""
        while ((bf.readLine().also { line = it })!=null){
            stringBuilder.append(line)
        }
    }catch (e: IOException){
        e.printStackTrace()
    }

    return stringBuilder.toString()
}

/**
 * 小数转毫秒
 */
fun decimalToMs(num:Float?):Int{

    return  (num!!*1000).toInt()
}

/**
 * 毫秒转 00:00  分 秒
 */

fun decimalToMinAndS( ms:Int):String{
    val total = ms/1000
    var second = total%60
    var minute = (total/60)%60

    return Formatter().format("%02d:%02d",minute,second).toString()
}

/**
 * 判断是鸿蒙系统
 */
fun isHarmonyOs():Boolean{
    return try {
        var buildExClass = Class.forName("com.huawei.system.BuildEx")
        var osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
        "Harmony".equals(osBrand.toString(),ignoreCase = true)
    } catch (e:Throwable) {
        false
    }
}

/**
 * 昵称
 */
var totalNickname = ""

/**
 * 跟读总分
 */
var totalReadScore = 0

/**
 * 游戏总分
 */
var totalGameScore = 0

/**
 * 问答总分
 */
var totalAnswerScore = 0

/**
 * 电话号码和随机数据组成的
 */
var totalSessionId = "000"

//当前关卡进度
var totalCurrentLevel = 0
val Context.inputMethodManager: InputMethodManager get() = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

//图片音频基本路径
val imgVoiceBasePath  = "https://ai.aidcstore.net/resource/"
/**
 * 分割图片的地址 eg  img:/profile/images/v2/cat.png
 */
fun getPicUrl(path:String):String{
    val paths = path.split(":")
   return  if (paths.size>1){
        paths[1]
    }else{
        ""
    }
}