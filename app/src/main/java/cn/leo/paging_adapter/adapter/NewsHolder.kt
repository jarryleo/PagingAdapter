package cn.leo.paging_adapter.adapter

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_adapter.image.loadImage
import cn.leo.paging_adapter.utils.dp
import cn.leo.paging_ktx.ItemHelper
import cn.leo.paging_ktx.SimpleHolder

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 知乎日报 holder
 */
class NewsHolder : SimpleHolder<NewsBean.StoriesBean>(R.layout.item_news) {
    override fun bindItem(
        helper: ItemHelper,
        data: NewsBean.StoriesBean,
        payloads: MutableList<Any>?
    ) {
        helper.setText(R.id.tv_title, data.title)
            .setImage(R.id.iv_cover) {
                loadImage(data.images?.get(0) ?: "", corners = 6.dp)
            }
    }
}