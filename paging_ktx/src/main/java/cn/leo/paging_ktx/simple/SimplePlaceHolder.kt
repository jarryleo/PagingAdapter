package cn.leo.paging_ktx.simple

import androidx.annotation.LayoutRes
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.adapter.ItemHelper

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : paging 占位holder
 * 当数据为空的时候 显示的占位holder，用来确认固定数量条目,以及展示占位图
 */
open class SimplePlaceHolder(@LayoutRes val layout: Int) :
    SimpleHolder<DifferData>(layout) {

    override fun bindItem(
        item: ItemHelper,
        data: DifferData,
        payloads: MutableList<Any>?
    ) {

    }

}