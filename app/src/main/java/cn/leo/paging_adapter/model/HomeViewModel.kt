package cn.leo.paging_adapter.model

import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import cn.leo.paging_adapter.activity.MainActivity
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimpleCheckedAdapter
import cn.leo.paging_ktx.simple.SimplePager

/**
 * @author : ling luo
 * @date : 2022/4/7
 * @description : 测试选择页model
 */
class HomeViewModel : ViewModel() {

    val pager = SimplePager<Long, DifferData>(viewModelScope) { param ->
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

    var state = State()
    var event = Event()

    class State(
        val isCheckedAll: ObservableBoolean = ObservableBoolean(false),

        val isCheckedMode: ObservableBoolean = ObservableBoolean(false)
    )

    inner class Event {

        fun jump(v: View) {
            v.context.startActivity(Intent(v.context, MainActivity::class.java))
        }

        fun switchMode(adapter: SimpleCheckedAdapter) {
            when {
                adapter.isMultiCheckedModel() -> {
                    adapter.closeCheckModel()
                }
                adapter.isSingleCheckedModel() -> {
                    adapter.closeCheckModel()
                }
                else -> {
                    adapter.setSingleCheckModel(false)
                    //adapter.setMultiCheckModel()
                }
            }
            state.isCheckedMode.set(adapter.isMultiCheckedModel() || adapter.isSingleCheckedModel())
        }

        fun checkedAll(adapter: SimpleCheckedAdapter) {
            if (adapter.isAllChecked()) {
                adapter.cancelChecked()
            } else {
                adapter.checkedAll()
            }
        }

        fun reverse(adapter: SimpleCheckedAdapter) {
            adapter.reverseChecked()
        }

    }
}