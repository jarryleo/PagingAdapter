package cn.leo.paging_ktx

import androidx.annotation.LayoutRes

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 简易holder
 */
abstract class SimpleHolder<T : DifferData> : PagingDataAdapterKtx.ItemHolder<T>() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

    final override fun bindData(
        helper: PagingDataAdapterKtx.ItemHelper,
        data: T?,
        payloads: MutableList<Any>?
    ) {
        if (data == null) return //简易holder data不为空
        bindItem(helper, data, payloads)
    }

    abstract fun bindItem(
        helper: PagingDataAdapterKtx.ItemHelper,
        data: T,
        payloads: MutableList<Any>?
    )
}