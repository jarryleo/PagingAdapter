package cn.leo.paging_adapter.repository

import androidx.paging.PagingSource
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimplePager
import kotlinx.coroutines.CoroutineScope

/**
 * @author : ling luo
 * @date : 2022/10/26
 * @description : 测试数据仓库
 */
class CheckListRepository {

    fun getPager(scope: CoroutineScope) =
        SimplePager<Long, DifferData>(scope) { param ->
            val key = param.key ?: 0L
            val list = (0..9)
                .map { it + key * 10 }
                .map { TitleBean("测试$it") }
            val nextKey = if (key > 3) {
                null
            } else {
                key + 1
            }
            PagingSource.LoadResult.Page(list, null, nextKey)
        }
}