package com.cds.childrensmall.adapter

import android.util.Log
import coil.load
import com.cds.childrensmall.R
import com.cds.childrensmall.databinding.AnswerItemBinding
import com.cds.childrensmall.model.bean.StartAnswerBean
import com.cds.childrensmall.util.getPicUrl
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

class AnswerAdapter() :BaseQuickAdapter<StartAnswerBean.NodeBean,BaseDataBindingHolder<AnswerItemBinding>>(R.layout.answer_item){
    override fun convert(holder: BaseDataBindingHolder<AnswerItemBinding>, item: StartAnswerBean.NodeBean) {
        holder.dataBinding?.answer?.load(
            "http://114.255.82.226:9313/prod-api${getPicUrl(item.content?:"")}"
        )

    }

}