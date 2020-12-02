package cn.leo.paging_adapter.bean

import cn.leo.paging_ktx.adapter.DifferData

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 标题分组
 */
data class TitleBean(
    val title: String
) : DifferData {

    override fun areItemsTheSame(data: DifferData): Boolean {
        return (data as? TitleBean)?.title == title
    }
}