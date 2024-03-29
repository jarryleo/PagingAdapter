package cn.leo.paging_adapter.model

import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.leo.paging_adapter.activity.MainActivity
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_adapter.db.RepoDatabase
import cn.leo.paging_adapter.db.RepoEntity
import cn.leo.paging_ktx.simple.SimpleCheckedAdapter
import cn.leo.paging_ktx.simple.SimplePager
import kotlinx.coroutines.launch

/**
 * @author : ling luo
 * @date : 2022/4/7
 * @description : 测试选择页model
 */
class HomeViewModel : ViewModel() {

    private val list = (0..50).map { TitleBean("测试$it") }
    val pager = SimplePager(viewModelScope,
        pagingSource = { RepoDatabase.instance.repoDao().get() }
    )

    var state = State()
    var event = Event()

    fun init() {

        val list = (0..50).map { RepoEntity(it, "测试DB$it") }
        viewModelScope.launch {
            RepoDatabase.instance.repoDao().clear()
            RepoDatabase.instance.repoDao().insert(list)
        }
    }

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
                adapter.isMultiCheckedMode() -> {
                    adapter.closeCheckMode()
                }
                adapter.isSingleCheckedMode() -> {
                    adapter.closeCheckMode()
                }
                else -> {
//                    adapter.setSingleCheckModel(false)
                    adapter.setMultiCheckMode()
                }
            }
            state.isCheckedMode.set(adapter.isMultiCheckedMode() || adapter.isSingleCheckedMode())
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

        fun del(adapter: SimpleCheckedAdapter) {
            viewModelScope.launch {
                adapter.getCheckedItemList().forEach {
                    //adapter.removeItem(it)
                    adapter.removeItem(it)
                    val item = it as? RepoEntity
                    item?.let {
                        RepoDatabase.instance.repoDao().delete(item)
                    }
                }
            }
        }

        fun insertHead() {
            val list = (-10..-1).map { RepoEntity(it, "测试DB$it") }
            viewModelScope.launch {
                RepoDatabase.instance.repoDao().insert(list)
            }
        }

        fun insertFoot() {
            val list = (51..60).map { RepoEntity(it, "测试DB$it") }
            viewModelScope.launch {
                RepoDatabase.instance.repoDao().insert(list)
            }
        }

    }
}