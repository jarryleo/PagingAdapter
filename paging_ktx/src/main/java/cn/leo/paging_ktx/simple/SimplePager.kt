package cn.leo.paging_ktx.simple

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 简易数据分页
 */
open class SimplePager<K : Any, V : Any>(
    private val scope: CoroutineScope,
    private val pageSize: Int = 20,
    private val initialLoadSize: Int = pageSize,
    private val prefetchDistance: Int = pageSize,
    private val maxSize: Int = PagingConfig.MAX_SIZE_UNBOUNDED,
    private val enablePlaceholders: Boolean = false,
    private val jumpThreshold: Int = PagingSource.LoadResult.Page.COUNT_UNDEFINED,
    private val initialKey: K? = null,
    private val pagingSource: () -> PagingSource<K, V>? = { null },
    private val refreshKey: (state: PagingState<K, V>) -> K? = { initialKey },
    private val loadData:
    suspend (PagingSource.LoadParams<K>) -> PagingSource.LoadResult<K, V>? = { null }
) {

    open fun getData(): Flow<PagingData<V>> {
        return Pager(
            PagingConfig(
                pageSize = pageSize,
                initialLoadSize = initialLoadSize,
                prefetchDistance = prefetchDistance,
                maxSize = maxSize,
                enablePlaceholders = enablePlaceholders,
                jumpThreshold = jumpThreshold
            ),
            initialKey = initialKey
        ) {
            pagingSource() ?: object : PagingSource<K, V>() {
                override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                    return loadData(params) ?: throw IllegalArgumentException(
                        "one of pagingSource or loadData must not null"
                    )
                }

                override fun getRefreshKey(state: PagingState<K, V>): K? {
                    return refreshKey(state)
                }
            }
        }.flow.cachedIn(scope)
    }

    fun getScope() = scope
}