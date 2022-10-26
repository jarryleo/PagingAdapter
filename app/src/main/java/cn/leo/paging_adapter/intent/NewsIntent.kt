package cn.leo.paging_adapter.intent

import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimplePager

/**
 * @author : ling luo
 * @date : 2022/10/25
 * @description : 新闻意图
 */

sealed class NewsIntent{
    data class NewsStates(
        val pager: SimplePager<*,DifferData>
    ):NewsIntent()
}
