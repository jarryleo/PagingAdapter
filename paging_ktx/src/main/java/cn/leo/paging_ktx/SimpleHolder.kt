package cn.leo.paging_ktx

import androidx.annotation.LayoutRes

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
        helper: ItemHelper,
        data: T?,
        payloads: MutableList<Any>?
    ) {
        if (data == null) return //简易holder data不为空
        bindItem(helper, data, payloads)
    }

    abstract fun bindItem(
        helper: ItemHelper,
        data: T,
        payloads: MutableList<Any>?
    )
}