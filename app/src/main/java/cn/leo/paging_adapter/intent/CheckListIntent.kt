package cn.leo.paging_adapter.intent

import androidx.databinding.ObservableBoolean
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimplePager

/**
 * @author : ling luo
 * @date : 2022/10/26
 * @description : 测试列表数据
 */
sealed class CheckListIntent {
    data class CheckList(val pager: SimplePager<*, DifferData>) : CheckListIntent()
    data class CheckedMode(
        val isCheckedAll: ObservableBoolean = ObservableBoolean(false),
        val isCheckedMode: ObservableBoolean = ObservableBoolean(false)
    ) : CheckListIntent()

    object Reverse : CheckListIntent()
}
