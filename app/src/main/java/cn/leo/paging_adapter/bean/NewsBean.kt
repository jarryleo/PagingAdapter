package cn.leo.paging_adapter.bean

import cn.leo.paging_ktx.DifferData

/**
 * @author : leo
 * @date : 2020/5/12
 */
data class NewsBean(
    var date: String? = "",
    var stories: List<StoriesBean> = emptyList()
) {

    data class StoriesBean(
        var type: Int = 0,
        var id: Int = 0,
        var ga_prefix: String? = null,
        var title: String? = null,
        var url: String? = null,
        var images: List<String>? = null
    ) : DifferData {
        override fun areItemsTheSame(d: DifferData): Boolean {
            return (d as? StoriesBean)?.id == id
        }

        override fun areContentsTheSame(d: DifferData): Boolean {
            return (d as? StoriesBean)?.title == title
        }
    }
}