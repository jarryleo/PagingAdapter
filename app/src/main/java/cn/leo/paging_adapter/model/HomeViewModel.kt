package cn.leo.paging_adapter.model

import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingSource
import cn.leo.paging_adapter.activity.MainActivity
import cn.leo.paging_adapter.base.MviDispatcher
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_adapter.intent.CheckListIntent
import cn.leo.paging_adapter.repository.CheckListRepository
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimpleCheckedAdapter
import cn.leo.paging_ktx.simple.SimplePager
import kotlinx.coroutines.launch

/**
 * @author : ling luo
 * @date : 2022/4/7
 * @description : 测试选择页model
 */
class HomeViewModel : MviDispatcher<CheckListIntent>() {

    override fun onCreate(owner: LifecycleOwner) {
        viewModelScope.launch {
            sendResult(CheckListIntent.CheckList(
                CheckListRepository().getPager(viewModelScope)
            ))
        }
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
//                    adapter.setSingleCheckModel(false)
                    adapter.setMultiCheckModel()
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