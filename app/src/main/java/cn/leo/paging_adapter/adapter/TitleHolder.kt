package cn.leo.paging_adapter.adapter

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_ktx.PagingDataAdapterKtx
import cn.leo.paging_ktx.SimpleHolder

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 标题holder
 */
class TitleHolder : SimpleHolder<TitleBean>() {

    override fun getLayoutRes(): Int {
        return R.layout.item_title
    }

    override fun bindItem(
        helper: PagingDataAdapterKtx.ItemHelper,
        data: TitleBean,
        payloads: MutableList<Any>?
    ) {
        helper.setText(R.id.tv_title, data.title)
    }

}