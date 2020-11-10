package cn.leo.paging_ktx

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * @author : ling luo
 * @date : 2020/11/10
 * @description : 简易数据分页
 */
abstract class SimplePager<K : Any, V : Any>(
    private val pageSize: Int = 20,
    private val initialKey: K? = null
) {

    abstract suspend fun loadData(params: PagingSource.LoadParams<K>):
            PagingSource.LoadResult<K, V>

    fun getData(scope: CoroutineScope): Flow<PagingData<V>> {
        return Pager(PagingConfig(pageSize, initialLoadSize = pageSize), initialKey = initialKey) {
            object : PagingSource<K, V>() {
                override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                    return loadData(params)
                }
            }
        }.flow.cachedIn(scope)
    }
}