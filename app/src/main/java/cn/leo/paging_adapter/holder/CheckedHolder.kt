package cn.leo.paging_adapter.holder

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_adapter.databinding.ItemCheckedBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_ktx.adapter.ItemHelper
import cn.leo.paging_ktx.ext.isChecked
import cn.leo.paging_ktx.simple.SimpleHolder

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 标题holder
 */
class CheckedHolder : SimpleHolder<TitleBean>(R.layout.item_checked) {
    override fun bindItem(item: ItemHelper, data: TitleBean, payloads: MutableList<Any>?) {
        item.binding<ItemCheckedBinding>()?.let {
            if (payloads.isNullOrEmpty()) it.data = data
            it.cbTitle.isChecked = item.isChecked()
        }
    }
}