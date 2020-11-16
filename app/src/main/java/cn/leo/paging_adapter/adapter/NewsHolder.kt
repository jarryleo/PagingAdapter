package cn.leo.paging_adapter.adapter

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_adapter.image.loadImage
import cn.leo.paging_adapter.utils.dp
import cn.leo.paging_ktx.PagingDataAdapterKtx
import cn.leo.paging_ktx.SimpleHolder
import kotlinx.android.synthetic.main.item_news.view.*

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 知乎日报 holder
 */
class NewsHolder : SimpleHolder<NewsBean.StoriesBean>() {

    override fun getLayoutRes(): Int {
        return R.layout.item_news
    }

    override fun bindItem(
        helper: PagingDataAdapterKtx.ItemHelper,
        data: NewsBean.StoriesBean,
        payloads: MutableList<Any>?
    ) {
        //设置数据方法，可以采用官方kotlin的方案（没有缓存，每次都采用findViewById,helper有缓存）
        helper.itemView.apply {
            tv_title.text = data.title
            iv_cover.loadImage(data.images?.get(0) ?: "", corners = 6.dp)
        }
        //也可以采用helper的id方案，helper里可以获取position和设置点击事件等
        /*helper.setText(R.id.tv_title, data.title)
            .findViewById<ImageView>(R.id.iv_cover)
            .loadImage(data.images?.get(0) ?: "", corners = 6.dp)*/
    }


}