package cn.leo.paging_adapter.model

import android.content.Intent
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.insertSeparators
import cn.leo.paging_adapter.activity.MainActivity
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_adapter.db.RepoEntity
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimpleCheckedAdapter
import cn.leo.paging_ktx.simple.SimpleListPager
import kotlinx.coroutines.flow.map

/**
 * @author : ling luo
 * @date : 2022/4/7
 * @description : 测试选择页model
 */
class HomeViewModel1 : ViewModel() {

    private val pager = SimpleListPager(viewModelScope, emptyList<DifferData>())
    val data = pager.getData().map {
        it.insertSeparators { differData: DifferData?, differData2: DifferData? ->
            if (differData !is RepoEntity) return@insertSeparators TitleBean("标题0-4")
            val next = differData2 as? RepoEntity ?: return@insertSeparators null
            val id = next.id
            if (id % 5 == 0) {
                return@insertSeparators TitleBean("标题$id-${id + 4}")
            }
            null
        }
    }

    var state = State()
    var event = Event()

    fun init() {
        val list = (0..50).map { RepoEntity(it, "测试DB$it") }
        pager.edit {
            it.clear()
            it.addAll(list)
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
            pager.edit {
                it.removeAll(adapter.getCheckedItemList())
            }
            adapter.cancelChecked()
        }

        fun insertHead() {
            pager.edit {
                val id = ((it.firstOrNull() as? RepoEntity)?.id ?: 0) - 1
                val item = RepoEntity(id, "头部添加$id")
                it.add(0, item)
            }
        }

        fun insertFoot() {
            pager.edit {
                val id = ((it.lastOrNull() as? RepoEntity)?.id ?: 0) + 1
                val item = RepoEntity(id, "尾部添加$id")
                it.add(item)
            }
        }

    }
}