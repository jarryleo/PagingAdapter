package cn.leo.paging_ktx.simple

import androidx.paging.PagingData
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.adapter.ItemHelper
import cn.leo.paging_ktx.adapter.PagingAdapter
import cn.leo.paging_ktx.ext.getSuperClassGenericType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 简易RvAdapter
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
open class SimplePagingAdapter(
    vararg holders: SimpleHolder<*>
) : PagingAdapter<DifferData>(
    itemCallback(
        areItemsTheSame = { old, new ->
            old.areItemsTheSame(new)
        },
        areContentsTheSame = { old, new ->
            old.areContentsTheSame(new)
        },
        getChangePayload = { old, new ->
            old.getChangePayload(new)
        }
    )
) {

    private val holderMap =
        mutableMapOf<Class<DifferData>, SimpleHolder<DifferData>?>()

    init {
        cacheHolder(holders)
    }

    fun addHolder(holder: SimpleHolder<*>) {
        val key = holder::class.java.getSuperClassGenericType<DifferData>()
        val value = holder as? SimpleHolder<DifferData>
        holderMap[key] = value
    }

    private fun cacheHolder(holders: Array<out SimpleHolder<*>>) {
        holders.forEach { addHolder(it) }
    }

    protected fun setHolder(key: Class<DifferData>, holder: SimpleHolder<DifferData>) {
        holderMap[key] = holder
    }

    open fun <T : DifferData> setList(scope: CoroutineScope, list: List<T>) {
        super.setPagingData(scope, PagingData.from(list))
    }

    open fun <T : DifferData> setData(scope: CoroutineScope, pagingData: PagingData<T>) {
        super.setPagingData(scope, pagingData as PagingData<DifferData>)
    }

    open fun <T : DifferData> setPager(pager: SimplePager<*, T>) {
        pager.getScope().launch {
            pager.getData().collectLatest {
                setData(this, it)
            }
        }
    }

    private fun getHolder(data: DifferData?): SimpleHolder<DifferData>? {
        val key = if (data == null) {
            DifferData::class.java
        } else {
            data::class.java
        }
        return holderMap[key]
    }

    override fun getItemLayout(position: Int): Int {
        //没有对应数据类型的holder
        val holder = getHolder(getData(position))
            ?: throw RuntimeException("SimplePagingAdapter : no match holder")
        return holder.getItemLayout(position)
    }

    override fun bindData(item: ItemHelper, data: DifferData?, payloads: MutableList<Any>?) {
        val holder = getHolder(data) ?: return
        item.setItemHolder(holder, payloads)
    }
}