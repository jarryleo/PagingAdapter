package cn.leo.paging_ktx.simple

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.adapter.ItemHelper
import kotlinx.coroutines.CoroutineScope

/**
 * @author : leo
 * @date : 2020/11/27
 * @description : 简易列表Adapter ，对标ListView 的 ArrayAdapter
 */
class SimpleArrayAdapter<T : DifferData>(
    scope: CoroutineScope,
    @LayoutRes val resource: Int,
    @IdRes val textViewResourceId: Int = View.NO_ID,
    val data: List<T> = emptyList()
) : SimplePagingAdapter() {

    init {
        val holder = ArrayHolder()
        if (data.isNotEmpty()) {
            setHolder(data[0].javaClass, holder)
            setList(scope, data)
        }
    }

    inner class ArrayHolder : SimpleHolder<DifferData>(resource) {
        override fun bindItem(
            item: ItemHelper,
            data: DifferData,
            payloads: MutableList<Any>?
        ) {
            val text = if (data is CharSequence) {
                data
            } else {
                data.toString()
            }
            val textView = if (textViewResourceId == View.NO_ID) {
                item.itemView as? TextView
            } else {
                item.findViewById(textViewResourceId)
            }
            textView?.text = text
        }
    }
}