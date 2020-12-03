package cn.leo.paging_adapter.adapter

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_adapter.image.loadImage
import cn.leo.paging_ktx.adapter.ItemHelper
import cn.leo.paging_ktx.simple.SimpleHolder
import kotlinx.android.synthetic.main.item_news.*

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
        tv_title.text = data.title
        iv_cover.loadImage(data.images?.get(0))
        item.addOnClickListener(tv_title)
    }
}