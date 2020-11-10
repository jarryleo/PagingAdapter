package cn.leo.paging_ktx

import androidx.annotation.LayoutRes

/**
 * @author : ling luo
 * @date : 2020/11/10
 * @description : 简易holder
 */
abstract class SimpleHolder<T : DifferData> : PagingDataAdapterKtx.ItemHolder<T>() {

    @LayoutRes
    abstract fun getLayoutRes(): Int

}