package cn.leo.paging_ktx

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * @author : ling luo
 * @date : 2020/11/10
 * @description : 简易数据分页
 */
class SimplePager<K : Any, V : Any>(
    private val scope: CoroutineScope,
    private val pageSize: Int = 20,
    private val initialKey: K? = null,
    private val loadData: suspend (PagingSource.LoadParams<K>) -> PagingSource.LoadResult<K, V>
) {

    fun getData(): Flow<PagingData<V>> {
        return Pager(
            PagingConfig(
                pageSize,
                initialLoadSize = pageSize,
                enablePlaceholders = false //SimplePager不支持占位条目
            ),
            initialKey = initialKey
        ) {
            object : PagingSource<K, V>() {
                override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                    return loadData(params)
                }
            }
        }.flow.cachedIn(scope)
    }

    fun getScope() = scope
}