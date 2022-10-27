package cn.leo.paging_adapter.bean

import cn.leo.paging_ktx.adapter.DifferData

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

        val image: String
            get() = images?.getOrNull(0) ?: ""

        override fun areItemsTheSame(data: DifferData): Boolean {
            return (data as? StoriesBean)?.id == id
        }

        override fun areContentsTheSame(data: DifferData): Boolean {
            return (data as? StoriesBean)?.title == title
        }
    }
}