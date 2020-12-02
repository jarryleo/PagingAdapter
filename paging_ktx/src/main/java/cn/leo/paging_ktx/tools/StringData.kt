package cn.leo.paging_ktx.tools

import cn.leo.paging_ktx.adapter.DifferData

/**
 * @author : leo
 * @date : 2020/11/27
 * @description : 字符串数据
 */
data class StringData(
    val string: CharSequence
) : DifferData {
    override fun areItemsTheSame(data: DifferData): Boolean {
        return (data as? StringData)?.string == string
    }

    override fun areContentsTheSame(data: DifferData): Boolean {
        return (data as? StringData)?.string == string
    }

    override fun toString(): String {
        return string.toString()
    }
}