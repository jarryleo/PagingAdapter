package cn.leo.paging_adapter.holder

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_adapter.databinding.ItemNewsBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_ktx.adapter.ItemHelper
import cn.leo.paging_ktx.simple.SimpleHolder

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 知乎日报 holder
 */
class NewsHolder : SimpleHolder<NewsBean.StoriesBean>(R.layout.item_news) {
    override fun bindItem(
        item: ItemHelper,
        data: NewsBean.StoriesBean,
        payloads: MutableList<Any>?
    ) {
        item.binding<ItemNewsBinding>()?.data = data
    }
}