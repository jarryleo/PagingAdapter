package cn.leo.paging_adapter.model

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_ktx.SimplePager
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : leo
 * @date : 2020/5/12
 */
class NewsViewModel : BaseViewModel() {

    private val mDate = Calendar.getInstance().apply {
        add(Calendar.DATE, 1)
    }

    private val initialKey = SimpleDateFormat("yyyyMMdd", Locale.CHINA)
        .format(mDate.time)
        .toLong()

    private val pager =
        object : SimplePager<Long, NewsBean.StoriesBean>(20, initialKey) {
            override suspend fun loadData(params: PagingSource.LoadParams<Long>):
                    PagingSource.LoadResult<Long, NewsBean.StoriesBean> {
                val date =
                    params.key ?: return PagingSource.LoadResult.Page(emptyList(), null, null)
                return try {
                    val data = api.getNews(date).await()
                    PagingSource.LoadResult.Page(data.stories, null, data.date?.toLongOrNull())
                } catch (e: Exception) {
                    PagingSource.LoadResult.Error(e)
                }
            }
        }

    val data = pager.getData(viewModelScope)
}