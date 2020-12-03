package cn.leo.paging_ktx.simple

import androidx.annotation.LayoutRes
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.adapter.ItemHelper
import cn.leo.paging_ktx.adapter.ItemHolder
import kotlinx.android.extensions.LayoutContainer

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 简易holder
 */
abstract class SimpleHolder<T : DifferData>(@LayoutRes val res: Int) :
    ItemHolder<T>() {

    @LayoutRes
    fun getLayoutRes(): Int = res

    final override fun bindData(
        item: ItemHelper,
        data: T?,
        payloads: MutableList<Any>?
    ) {
        if (data == null) return //简易holder data不为空
        bindItem(item, data, payloads)
    }

    abstract fun bindItem(
        item: ItemHelper,
        data: T,
        payloads: MutableList<Any>?
    )
}