package cn.leo.paging_adapter.model

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import cn.leo.paging_adapter.base.MviDispatcher
import cn.leo.paging_adapter.intent.NewsIntent
import cn.leo.paging_adapter.repository.NewsRepository
import kotlinx.coroutines.launch

/**
 * @author : leo
 * @date : 2020/5/12
 */
class NewsViewModel : MviDispatcher<NewsIntent>() {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        viewModelScope.launch {
            sendResult(
                NewsIntent.NewsStates(
                    NewsRepository().getPager(viewModelScope)
                )
            )
        }
    }

    override suspend fun onHandle(event: NewsIntent) {
        super.onHandle(event)
        when (event) {
            NewsIntent.TEST -> {
                Log.e("onHandle", "onHandle:test ")
            }
        }

    }
}