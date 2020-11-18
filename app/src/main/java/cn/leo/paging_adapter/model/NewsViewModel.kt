package cn.leo.paging_adapter.model

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_ktx.DifferData
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

    val pager = SimplePager<Long, DifferData>(
        viewModelScope,
        enablePlaceholders = true
    ) {
        val date = it.key ?: initialKey
        try {
            //从网络获取数据
            val data = api.getNews(date).await()
            //添加title
            val list: MutableList<DifferData> = data.stories.toMutableList()
            list.add(0, TitleBean(date.toString()))
            //返回数据
            PagingSource.LoadResult.Page(
                list,
                null,
                data.date?.toLongOrNull(),
                0,  //前面剩余多少未加载数量，
                100  //后面剩余多少未加载数量，配合 enablePlaceholders 在滑动过快的时候显示占位；
            )
        } catch (e: Exception) {
            //请求失败
            PagingSource.LoadResult.Error(e)
        }
    }

}