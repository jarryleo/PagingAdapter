package cn.leo.paging_adapter.model

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

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        viewModelScope.launch {
            sendResult(
                NewsIntent.NewsStates(
                    NewsRepository().getPager(viewModelScope)
                )
            )
        }
    }
}