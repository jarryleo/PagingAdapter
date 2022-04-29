package cn.leo.paging_ktx.simple

import androidx.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * 这是一个不分页但是可以修改数据的数据源
 */
open class SimpleListPager<V : Any>(
    private val scope: CoroutineScope,
    list: List<V>
) : SimplePager<Int, V>(scope) {

    private var data = list.toMutableList()

    private var pagingSource: PagingSource<Int, V>? = null

    private fun getPagingSource(): PagingSource<Int, V> {
        return object : PagingSource<Int, V>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
                return LoadResult.Page(data, null, null)
            }

            override fun getRefreshKey(state: PagingState<Int, V>): Int {
                return 0
            }
        }
    }

    override fun getData(): Flow<PagingData<V>> {
        return Pager(
            PagingConfig(
                pageSize = data.size
            ),
            pagingSourceFactory = {
                val ps = getPagingSource()
                pagingSource = ps
                ps
            }
        ).flow.cachedIn(scope)
    }

    open fun edit(block: (MutableList<V>) -> Unit) {
        val copy = data.toMutableList()
        block(copy)
        data = copy
        pagingSource?.invalidate()
    }

}