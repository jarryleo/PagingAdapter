package cn.leo.paging_ktx

import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import kotlinx.coroutines.CoroutineScope

/**
 * @author : leo
 * @date : 2020/11/27
 * @description : 简易列表Adapter ，对标ListView 的 ArrayAdapter
 */
class ArrayPagingAdapter<T : DifferData>(
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
            helper: ItemHelper,
            data: DifferData,
            payloads: MutableList<Any>?
        ) {
            val text = if (data is CharSequence) {
                data
            } else {
                data.toString()
            }
            val textView = if (textViewResourceId == View.NO_ID) {
                helper.itemView as? TextView
            } else {
                helper.findViewById(textViewResourceId)
            }
            textView?.text = text
        }
    }
}