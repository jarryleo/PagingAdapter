package cn.leo.paging_ktx.ext

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.adapter.PagingAdapter
import cn.leo.paging_ktx.simple.SimpleHolder
import cn.leo.paging_ktx.simple.SimplePager
import cn.leo.paging_ktx.simple.SimplePagingAdapter
import cn.leo.paging_ktx.tools.FloatDecoration

/**
 * @author : ling luo
 * @date : 2022/3/24
 * @description : RecyclerView 拓展
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
    fun onItemClick(onClick: (T) -> Unit)
    fun onItemLongClick(onClick: (T) -> Unit)
    fun onItemChildClick(onClick: (T) -> Unit)
    fun onItemChildLongClick(onClick: (T) -> Unit)
}

class DslClickBuilderImpl<T : DifferData>
    (val adapter: SimplePagingAdapter, val clazz: Class<T>) : DslClickBuilder<T> {

    override fun onItemClick(onClick: (T) -> Unit) {
        adapter.setOnItemClickListener { adapter, v, position ->
            onClick(adapter, v, position, onClick)
        }
    }

    override fun onItemLongClick(onClick: (T) -> Unit) {
        adapter.setOnItemLongClickListener { adapter, v, position ->
            onClick(adapter, v, position, onClick)
        }
    }

    override fun onItemChildClick(onClick: (T) -> Unit) {
        adapter.setOnItemChildClickListener { adapter, v, position ->
            onClick(adapter, v, position, onClick)
        }
    }

    override fun onItemChildLongClick(onClick: (T) -> Unit) {
        adapter.setOnItemChildLongClickListener { adapter, v, position ->
            onClick(adapter, v, position, onClick)
        }
    }

    private fun onClick(
        adapter: PagingAdapter<out Any>,
        v: View,
        position: Int,
        onClick: (T) -> Unit
    ) {
        val item = adapter.getData(position) ?: return
        if (item::class.java == clazz) {
            onClick(item as T)
        }
    }
}

class DslSimpleAdapterImpl(val recyclerView: RecyclerView) : DslSimpleAdapterBuilder {
    val adapter = SimplePagingAdapter()
    var mLayoutManager: RecyclerView.LayoutManager =
        LinearLayoutManager(recyclerView.context)

    override fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isFloatItem: Boolean,
        dsl: (@ClickDsl DslClickBuilder<T>.() -> Unit)?
    ) {
        val clickBuilder = DslClickBuilderImpl<T>(adapter, holder.getDataClassType())
        adapter.addHolder(holder)
        if (dsl != null) {
            clickBuilder.dsl()
        }
        if (isFloatItem) {
            recyclerView.addItemDecoration(FloatDecoration(holder.getLayoutRes()))
        }
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