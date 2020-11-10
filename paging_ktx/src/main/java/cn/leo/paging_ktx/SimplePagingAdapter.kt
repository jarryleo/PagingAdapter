package cn.leo.paging_ktx

import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData

/**
 * @author : ling luo
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
    private val holderList =
        mutableListOf<SimpleHolder<*>>()

    init {
        this.holderList += holders
    }

    fun <T : DifferData> setData(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        super.setPagingData(lifecycle, pagingData as PagingData<DifferData>)
    }

    private fun getHolder(data: DifferData?): SimpleHolder<DifferData>? {
        val differData = data ?: return null
        return holderList.firstOrNull {
            differData::class.java.name ==
                    it::class.java.getSuperClassGenericType<SimpleHolder<*>>().name
        } as? SimpleHolder<DifferData>
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