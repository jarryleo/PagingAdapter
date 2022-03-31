package cn.leo.paging_ktx.ext

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.simple.SimpleCheckedAdapter
import cn.leo.paging_ktx.simple.SimpleHolder
import cn.leo.paging_ktx.simple.SimplePager
import cn.leo.paging_ktx.simple.SimplePagingAdapter
import cn.leo.paging_ktx.tools.FloatDecoration

/**
 * @author : ling luo
 * @date : 2022/3/24
 * @description : PagingAdapter dsl拓展
 */

//作用域进行限制
@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class ClickDsl

interface DslAdapterBuilder {
    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager)

    fun <T : DifferData> setPager(pager: SimplePager<*, T>)
}

interface DslSimpleAdapterBuilder : DslAdapterBuilder {
    fun getAdapter(): SimplePagingAdapter
    fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isFloatItem: Boolean = false,
        dsl: (@ClickDsl DslClickBuilder<T>.() -> Unit)? = null
    )
}

interface DslSimpleCheckedAdapterBuilder : DslAdapterBuilder {
    fun getAdapter(): SimpleCheckedAdapter
    fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isClickChecked: Boolean = true,
        isFloatItem: Boolean = false,
        dsl: (@ClickDsl DslCheckedBuilder<T>.() -> Unit)? = null
    )
}

interface DslClickBuilder<T : DifferData> {
    fun onItemClick(onClick: OnItemClick<T>)
    fun onItemLongClick(onClick: OnItemClick<T>)
    fun onItemChildClick(@IdRes viewId: Int, onClick: OnItemClick<T>)
    fun onItemChildLongClick(@IdRes viewId: Int, onClick: OnItemClick<T>)
}

interface DslCheckedBuilder<T : DifferData> : DslClickBuilder<T> {
    fun onChecked(onChecked: OnItemChecked)
    fun setChecked(position: Int, isChecked: Boolean)
    fun getCheckedPositionList(): List<Int>
    fun getCheckedItemList(): List<T>
}

data class ItemInfo<T : DifferData>(
    val data: T,
    val position: Int,
    val view: View,
    val adapter: SimplePagingAdapter,
    val recyclerView: RecyclerView,
)

/**
 * 条目选择拓展属性
 */
var <T : DifferData> ItemInfo<T>.isChecked: Boolean
    get() {
        if (adapter is SimpleCheckedAdapter) {
            return adapter.itemIsChecked(position)
        }
        return false
    }
    set(value) {
        if (adapter is SimpleCheckedAdapter) {
            adapter.setChecked(position, value)
        }
    }

data class CheckedInfo(
    val position: Int,
    val isChecked: Boolean,
    val isAllChecked: Boolean,
    val checkedCount: Int,
    val allCanCheckedCount: Int,
    val adapter: SimplePagingAdapter,
    val recyclerView: RecyclerView,
)

fun interface OnItemClick<T : DifferData> {
    fun onClick(itemInfo: ItemInfo<T>)
}

fun interface OnItemChecked {
    fun onChecked(checkedInfo: CheckedInfo)
}

@Suppress("UNCHECKED_CAST")
open class DslClickBuilderImpl<T : DifferData>(
    private val holder: SimpleHolder<T>
) : DslClickBuilder<T> {
    protected val clazz = holder.getDataClassType()

    private var onItemClickListener: OnItemClick<T>? = null
    private var onItemLongClickListener: OnItemClick<T>? = null
    private var onItemChildClickListener = mutableMapOf<Int, OnItemClick<T>>()
    private var onItemChildLongClickListener = mutableMapOf<Int, OnItemClick<T>>()

    open fun doItemClick(
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

class DslCheckedBuilderImpl<T : DifferData>(
    private val adapter: SimpleCheckedAdapter,
    private val isClickChecked: Boolean,
    holder: SimpleHolder<T>
) : DslClickBuilderImpl<T>(holder), DslCheckedBuilder<T> {

    internal var onCheckedCallback: OnItemChecked? = null

    override fun doItemClick(
        position: Int,
        v: View,
        adapter: SimplePagingAdapter,
        recyclerView: RecyclerView
    ) {
        super.doItemClick(position, v, adapter, recyclerView)
        if (!isClickChecked) return
        val item = adapter.getData(position)
        if (item != null && item::class.java == clazz) {
            if (adapter is SimpleCheckedAdapter) {
                adapter.setChecked(position, !adapter.itemIsChecked(position))
            }
        }
    }

    override fun onChecked(onChecked: OnItemChecked) {
        onCheckedCallback = onChecked
    }

    override fun setChecked(position: Int, isChecked: Boolean) {
        adapter.setChecked(position, isChecked)
    }

    override fun getCheckedPositionList(): List<Int> {
        return adapter.getCheckedPositionList()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getCheckedItemList(): List<T> {
        return adapter.getCheckedPositionList()
            .map { adapter.getData(it) as T }
    }
}

class ClickEventStore(val recyclerView: RecyclerView, val adapter: SimplePagingAdapter) {

    private val clickEventList = mutableListOf<DslClickBuilderImpl<*>>()

    fun addClickEvent(clickBuilder: DslClickBuilderImpl<*>) {
        clickEventList.add(clickBuilder)
    }

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

    override fun getAdapter(): SimplePagingAdapter {
        return adapter
    }

    override fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isFloatItem: Boolean,
        dsl: (@ClickDsl DslClickBuilder<T>.() -> Unit)?
    ) {
        val clickBuilder = DslClickBuilderImpl(holder)
        clickEventStore.addClickEvent(clickBuilder)
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

class DslSimpleCheckedAdapterImpl(val recyclerView: RecyclerView) : DslSimpleCheckedAdapterBuilder {
    internal val adapter = SimpleCheckedAdapter()
    internal var mLayoutManager: RecyclerView.LayoutManager =
        LinearLayoutManager(recyclerView.context)

    private val clickEventStore = ClickEventStore(recyclerView, adapter)

    private val checkedEventList = mutableListOf<OnItemChecked>()

    init {
        adapter.setOnCheckedCallback { position, isChecked,
                                       isAllChecked, checkedCount,
                                       allCanCheckedCount ->
            val checkedInfo = CheckedInfo(
                position, isChecked, isAllChecked,
                checkedCount, allCanCheckedCount,
                adapter, recyclerView
            )
            checkedEventList.forEach {
                it.onChecked(checkedInfo)
            }
        }
    }

    override fun getAdapter(): SimpleCheckedAdapter {
        return adapter
    }

    override fun <T : DifferData> addHolder(
        holder: SimpleHolder<T>,
        isClickChecked: Boolean,
        isFloatItem: Boolean,
        dsl: (@ClickDsl DslCheckedBuilder<T>.() -> Unit)?
    ) {
        val clickBuilder = DslCheckedBuilderImpl(
            adapter,
            isClickChecked,
            holder
        )
        clickEventStore.addClickEvent(clickBuilder)
        if (dsl != null) {
            clickBuilder.dsl()
        }
        if (isFloatItem) {
            recyclerView.addItemDecoration(FloatDecoration(holder.getItemLayout()))
        }
        val onCheckedCallback = clickBuilder.onCheckedCallback
        if (onCheckedCallback != null) {
            checkedEventList.add(onCheckedCallback)
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

/**
 * 构建简易列表适配器
 */
@Suppress("UNUSED")
fun RecyclerView.buildAdapter(init: @ClickDsl DslSimpleAdapterBuilder.() -> Unit): SimplePagingAdapter {
    val dslSimpleAdapterImpl = DslSimpleAdapterImpl(this)
    layoutManager = dslSimpleAdapterImpl.mLayoutManager
    adapter = dslSimpleAdapterImpl.adapter
    dslSimpleAdapterImpl.init()
    return dslSimpleAdapterImpl.adapter
}

/**
 * 构建简易选择列表适配器
 */
@Suppress("UNUSED")
fun RecyclerView.buildCheckedAdapter(init: @ClickDsl DslSimpleCheckedAdapterBuilder.() -> Unit): SimpleCheckedAdapter {
    val dslSimpleAdapterImpl = DslSimpleCheckedAdapterImpl(this)
    layoutManager = dslSimpleAdapterImpl.mLayoutManager
    adapter = dslSimpleAdapterImpl.adapter
    dslSimpleAdapterImpl.init()
    return dslSimpleAdapterImpl.adapter
}