package cn.leo.paging_ktx

import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : 简易RvAdapter
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class SimplePagingAdapter(
    vararg holders: SimpleHolder<*>
) : PagingDataAdapterKtx<DifferData>(
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
        holders.forEach {
            val key = it::class.java.getSuperClassGenericType<DifferData>()
            val value = it as? SimpleHolder<DifferData>
            holderMap[key] = value
        }
    }

    fun <T : DifferData> setList(scope: CoroutineScope, list: List<T>) {
        super.setPagingData(scope, PagingData.from(list))
    }

    fun <T : DifferData> setData(scope: CoroutineScope, pagingData: PagingData<T>) {
        super.setPagingData(scope, pagingData as PagingData<DifferData>)
    }

    fun <T : DifferData> setPager(pager: SimplePager<*, T>) {
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
        return holder.getLayoutRes()
    }

    override fun bindData(helper: ItemHelper, data: DifferData?, payloads: MutableList<Any>?) {
        val holder = getHolder(data) ?: return
        helper.setItemHolder(holder)
    }
}