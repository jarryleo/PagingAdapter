package cn.leo.paging_ktx

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope

/**
 * @author : leo
 * @date : 2020/11/16
 * @description : Recycler 拓展
 */

/**
 * 线性列表指定条目滑动到顶部第一个可见位置
 */
fun RecyclerView.smoothScrollToPositionWithTop(position: Int) {
    val topScroller = TopSmoothScroller(context)
    topScroller.targetPosition = position
    layoutManager?.startSmoothScroll(topScroller)
}

/**
 * 自定义线性滑动器
 */
class TopSmoothScroller(context: Context) : LinearSmoothScroller(context) {
    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun getVerticalSnapPreference(): Int {
        return SNAP_TO_START
    }
}

/**
 * 文字数组转StringData列表
 */
fun Array<out CharSequence>.toStringDataList() =
    this.map { StringData(it) }

/**
 * 文字列表转StringData列表
 */
fun List<CharSequence>.toStringDataList() =
    this.map { StringData(it) }

/**
 * 给RecyclerView设置简易的 文字列表
 * @param lifecycleOwner activity 或者 fragment
 * @param resource 条目布局
 * @param textViewResourceId 文本框id 可以不填，不填则是条目自身，条目自身不是文本框则报错
 * @param orientation 列表方向 @{RecyclerView.VERTICAL} @{RecyclerView.HORIZONTAL}
 * @param data 数据列表，利用@{toStringDataList()} 转变成文字列表
 */
inline fun <reified T : DifferData> RecyclerView.arrayAdapter(
    lifecycleOwner: LifecycleOwner,
    @LayoutRes resource: Int,
    @IdRes textViewResourceId: Int = View.NO_ID,
    orientation: Int = RecyclerView.VERTICAL,
    data: List<T> = emptyList()
) {
    val context: Activity? = when (lifecycleOwner) {
        is Activity -> lifecycleOwner
        is Fragment -> lifecycleOwner.requireActivity()
        else -> null
    }
    if (context != null) {
        this.layoutManager = LinearLayoutManager(context, orientation, false)
    }
    val scope: CoroutineScope? = when (lifecycleOwner) {
        is Activity -> lifecycleOwner.lifecycleScope
        is Fragment -> lifecycleOwner.lifecycleScope
        else -> null
    }
    if (scope != null) {
        this.adapter = ArrayPagingAdapter(scope, resource, textViewResourceId, data)
    }
}