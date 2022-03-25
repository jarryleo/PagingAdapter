package cn.leo.paging_ktx.ext

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimpleHolder
import cn.leo.paging_ktx.simple.SimplePager
import cn.leo.paging_ktx.simple.SimplePagingAdapter
import cn.leo.paging_ktx.tools.FloatDecoration

/**
 * @author : ling luo
 * @date : 2022/3/24
 * @description : RecyclerView dsl拓展
 */

//作用域进行限制
@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class ClickDsl

interface DslSimpleAdapterBuilder {
    fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isFloatItem: Boolean = false,
        dsl: (@ClickDsl DslClickBuilder<T>.() -> Unit)? = null
    )

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager)

    fun <T : DifferData> setPager(pager: SimplePager<*, T>)
}

interface DslClickBuilder<T : DifferData> {
    fun onItemClick(onClick: OnItemClick<T>)
    fun onItemLongClick(onClick: OnItemClick<T>)
    fun onItemChildClick(@IdRes viewId: Int, onClick: OnItemClick<T>)
    fun onItemChildLongClick(@IdRes viewId: Int, onClick: OnItemClick<T>)
}

data class ItemInfo<T : DifferData>(
    val data: T,
    val position: Int,
    val view: View,
    val adapter: SimplePagingAdapter,
    val recyclerView: RecyclerView,
)

fun interface OnItemClick<T : DifferData> {
    fun onClick(itemInfo: ItemInfo<T>)
}

@Suppress("UNCHECKED_CAST")
class DslClickBuilderImpl<T : DifferData>(
    private val holder: SimpleHolder<T>,
    clickEventStore: ClickEventStore
) : DslClickBuilder<T> {
    private val clazz = holder.getDataClassType()

    private var onItemClickListener: OnItemClick<T>? = null
    private var onItemLongClickListener: OnItemClick<T>? = null
    private var onItemChildClickListener = mutableMapOf<Int, OnItemClick<T>>()
    private var onItemChildLongClickListener = mutableMapOf<Int, OnItemClick<T>>()

    init {
        clickEventStore.clickEventList.add(this)
    }

    fun doItemClick(
        position: Int,
        v: View,
        adapter: SimplePagingAdapter,
        recyclerView: RecyclerView
    ) {
        val item = adapter.getData(position)
        if (item != null && item::class.java == clazz) {
            val itemInfo = ItemInfo(item as T, position, v, adapter, recyclerView)
            onItemClickListener?.onClick(itemInfo)
        }
    }

    fun doItemLongClick(
        position: Int,
        v: View,
        adapter: SimplePagingAdapter,
        recyclerView: RecyclerView
    ) {
        val item = adapter.getData(position)
        if (item != null && item::class.java == clazz) {
            val itemInfo = ItemInfo(item as T, position, v, adapter, recyclerView)
            onItemLongClickListener?.onClick(itemInfo)
        }
    }

    fun doItemChildClick(
        position: Int,
        v: View,
        adapter: SimplePagingAdapter,
        recyclerView: RecyclerView
    ) {
        val item = adapter.getData(position)
        if (item != null && item::class.java == clazz) {
            val itemInfo = ItemInfo(item as T, position, v, adapter, recyclerView)
            onItemChildClickListener[v.id]?.onClick(itemInfo)
        }
    }

    fun doItemChildLongClick(
        position: Int,
        v: View,
        adapter: SimplePagingAdapter,
        recyclerView: RecyclerView
    ) {
        val item = adapter.getData(position)
        if (item != null && item::class.java == clazz) {
            val itemInfo = ItemInfo(item as T, position, v, adapter, recyclerView)
            onItemChildLongClickListener[v.id]?.onClick(itemInfo)
        }
    }

    override fun onItemClick(onClick: OnItemClick<T>) {
        onItemClickListener = onClick
    }

    override fun onItemLongClick(onClick: OnItemClick<T>) {
        onItemLongClickListener = onClick
    }

    override fun onItemChildClick(@IdRes viewId: Int, onClick: OnItemClick<T>) {
        holder.itemChildClickIds.add(viewId)
        onItemChildClickListener[viewId] = onClick
    }

    override fun onItemChildLongClick(@IdRes viewId: Int, onClick: OnItemClick<T>) {
        holder.itemChildLongClickIds.add(viewId)
        onItemChildLongClickListener[viewId] = onClick
    }

}

class ClickEventStore(val recyclerView: RecyclerView, val adapter: SimplePagingAdapter) {

    val clickEventList = mutableListOf<DslClickBuilderImpl<*>>()

    init {
        adapter.setOnItemClickListener { _, v, position ->
            clickEventList.forEach {
                it.doItemClick(position, v, adapter, recyclerView)
            }
        }
        adapter.setOnItemLongClickListener { _, v, position ->
            clickEventList.forEach {
                it.doItemLongClick(position, v, adapter, recyclerView)
            }
        }
        adapter.setOnItemChildClickListener { _, v, position ->
            clickEventList.forEach {
                it.doItemChildClick(position, v, adapter, recyclerView)
            }
        }
        adapter.setOnItemChildLongClickListener { _, v, position ->
            clickEventList.forEach {
                it.doItemChildLongClick(position, v, adapter, recyclerView)
            }
        }
    }
}

class DslSimpleAdapterImpl(val recyclerView: RecyclerView) : DslSimpleAdapterBuilder {
    internal val adapter = SimplePagingAdapter()
    internal var mLayoutManager: RecyclerView.LayoutManager =
        LinearLayoutManager(recyclerView.context)

    private val clickEventStore = ClickEventStore(recyclerView, adapter)

    override fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isFloatItem: Boolean,
        dsl: (@ClickDsl DslClickBuilder<T>.() -> Unit)?
    ) {
        val clickBuilder = DslClickBuilderImpl(holder, clickEventStore)
        if (dsl != null) {
            clickBuilder.dsl()
        }
        if (isFloatItem) {
            recyclerView.addItemDecoration(FloatDecoration(holder.getItemLayout()))
        }
        adapter.addHolder(holder)
    }

    override fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        mLayoutManager = layoutManager
    }

    override fun <T : DifferData> setPager(pager: SimplePager<*, T>) {
        adapter.setPager(pager)
    }
}

fun RecyclerView.buildAdapter(init: @ClickDsl DslSimpleAdapterBuilder.() -> Unit): SimplePagingAdapter {
    val dslSimpleAdapterImpl = DslSimpleAdapterImpl(this)
    dslSimpleAdapterImpl.init()
    layoutManager = dslSimpleAdapterImpl.mLayoutManager
    adapter = dslSimpleAdapterImpl.adapter
    return dslSimpleAdapterImpl.adapter
}