package cn.leo.paging_adapter.ext

import android.view.View
import android.widget.Checkable
import cn.leo.paging_adapter.R

/**
 * 防重复点击拓展
 */
inline fun <T : View> T.singleClick(time: Long = 800, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            block(this)
        }
    }
}

/**
 * 兼容点击事件设置为this的情况
 */
@Suppress("unused")
fun <T : View> T.singleClick(onClickListener: View.OnClickListener, time: Long = 800) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            onClickListener.onClick(this)
        }
    }
}

var <T : View> T.lastClickTime: Long
    set(value) = setTag(R.id.single_click_id, value)
    get() = getTag(R.id.single_click_id) as? Long ?: 0