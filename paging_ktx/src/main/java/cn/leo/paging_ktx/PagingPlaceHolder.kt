package cn.leo.paging_ktx

import androidx.annotation.LayoutRes

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : paging 占位holder
 * 当数据为空的时候 显示的占位holder，用来确认固定数量条目
 */
class PagingPlaceHolder(@LayoutRes val layout: Int) :
    SimpleHolder<DifferData>(layout) {

    override fun bindItem(
        helper: PagingDataAdapterKtx.ItemHelper,
        data: DifferData,
        payloads: MutableList<Any>?
    ) {

    }

}