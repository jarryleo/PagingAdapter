package cn.leo.paging_ktx.simple

import android.util.Log
import androidx.annotation.IntRange
import cn.leo.paging_ktx.adapter.CheckedData
import cn.leo.paging_ktx.adapter.DifferData

/**
 * @author : ling luo
 * @date : 2022/3/30
 * @description : 可选择的 adapter
 *
 * 需要解决分页加载 和全选冲突问题
 *
 */
@Suppress("UNUSED")
open class SimpleCheckedAdapter : SimplePagingAdapter() {
    enum class CheckedMode {
        NONE, SINGLE, MULTI  //选择模式，无，单选/多选
    }

    private var checkedMode = CheckedMode.NONE  //当前选择模式

    private var singleCheckedItem: DifferData? = null //单选条目

    private var singleModeCancelable = false  //单选是否可取消选择

    private var multiCheckedList: MutableSet<DifferData> = mutableSetOf() //多选条目

    private var maxCheckedNum = Int.MAX_VALUE  //多选限制，最多选择多少个

    private var onCheckedCallback: OnCheckedCallback? = null //多选回调

    private var onMaxSelectCallback: OnMaxSelectCallback? = null //超过最多选择回调

    fun interface OnCheckedCallback {
        fun onChecked(
            position: Int,
            isChecked: Boolean,
            isAllChecked: Boolean,
            checkedCount: Int, //分页加载会变化
            allCanCheckedCount: Int //分页加载会变化
        )
    }

    /**
     * 超过最多选择
     */
    fun interface OnMaxSelectCallback {
        fun onSelectOverMax(max: Int)
    }

    /**
     * 设置选择回调
     */
    fun setOnCheckedCallback(callback: OnCheckedCallback) {
        onCheckedCallback = callback
    }

    /**
     * 更新所有条目
     */
    private fun notifyAllItem() {
        notifyItemRangeChanged(
            0,
            itemCount,
            checkedMode
        )
    }

    /**
     * 设置选择模式
     */
    private fun setCheckMode(mode: CheckedMode) {
        checkedMode = mode
        notifyAllItem()
    }

    /**
     * 关闭选择模式
     */
    open fun closeCheckMode() {
        setCheckMode(CheckedMode.NONE)
    }

    /**
     * 设置单选模式
     * @param cancelable 是否可取消选择
     */
    open fun setSingleCheckMode(cancelable: Boolean = true) {
        singleModeCancelable = cancelable
        if (!cancelable && singleCheckedItem == null) {
            singleCheckedItem = getData(0)
        }
        setCheckMode(CheckedMode.SINGLE)
    }

    /**
     * 设置多选模式
     */
    open fun setMultiCheckMode() {
        setCheckMode(CheckedMode.MULTI)
    }

    /**
     * 是否是单选模式
     */
    open fun isSingleCheckedMode(): Boolean {
        return checkedMode == CheckedMode.SINGLE
    }

    /**
     * 是否是多选模式
     */
    open fun isMultiCheckedMode(): Boolean {
        return checkedMode == CheckedMode.MULTI
    }

    /**
     * 设置最大选择数
     * @param max 最大选择数
     * @param onMaxSelectCallback 超过最大选择回调
     */
    open fun setMaxChecked(
        @IntRange(from = 1L) max: Int,
        onMaxSelectCallback: OnMaxSelectCallback? = null
    ) {
        this.maxCheckedNum = max
        this.onMaxSelectCallback = onMaxSelectCallback
        if (multiCheckedList.size > max) {
            cancelChecked()
        }
    }

    /**
     * 反选
     */
    open fun reverseChecked() {
        if (checkedMode != CheckedMode.MULTI) return
        //获取所有是CheckedData的数据
        val left = snapshot()
            .asSequence()
            .filter { it is CheckedData }
            .toMutableSet()
        left.removeAll(multiCheckedList)
        val copy = multiCheckedList.toMutableSet()
        copy.forEach {
            setChecked(it, false)
        }
        left.filterNotNull().forEach {
            setChecked(it, true)
        }
    }

    /**
     * 全选
     */
    open fun checkedAll() {
        if (checkedMode != CheckedMode.MULTI) return
        val all = snapshot().filterIsInstance<CheckedData>().toMutableSet()
        //排除已选中的条目，剩余全部设置选中
        val left = all.toMutableSet()
        left.removeAll(multiCheckedList)
        for (i in left) {
            if (!setChecked(i, true)) {
                return
            }
        }
    }

    /**
     * 取消选择
     */
    open fun cancelChecked() {
        singleCheckedItem = null
        val copy = multiCheckedList.toMutableSet()
        copy.forEach {
            setChecked(it, false)
        }
    }

    /**
     * 设置条目选择状态
     * @param item 条目对象
     * @param isChecked 是否选择
     * @return 是否改变状态
     */
    open fun setChecked(item: DifferData, isChecked: Boolean): Boolean {
        val position = snapshot().indexOf(item)
        return setChecked(position, isChecked)
    }

    /**
     * 设置条目选择状态
     * @param position 条目索引
     * @param isChecked 是否选择
     * @return 是否改变状态
     */
    open fun setChecked(position: Int, isChecked: Boolean): Boolean {
        val item = getData(position)
        //非可选择条目，不可选择
        if (item !is CheckedData) {
            Log.e("SimpleCheckedAdapter", "entity is not implement CheckedData")
            return false
        }
        when (checkedMode) {
            CheckedMode.SINGLE -> {
                if (!singleModeCancelable && !isChecked) return false
                val singleCheckedData = singleCheckedItem
                if (singleCheckedData == item && isChecked) return false
                if (singleCheckedData != null) {
                    notifyItemChanged(position)
                }
                singleCheckedItem = if (isChecked) item else null
                notifyItemChanged(position)
                onCheckedCallback?.onChecked(
                    position,
                    isChecked,
                    isAllChecked(),
                    if (singleCheckedData == null) 0 else 1,
                    canCheckedItemCount()
                )
                return true
            }
            CheckedMode.MULTI -> {
                val contains = multiCheckedList.contains(item)
                if (contains == isChecked) return false
                if (isChecked) {
                    return if (multiCheckedList.size < maxCheckedNum) {
                        multiCheckedList.add(item)
                        notifyItemChanged(position, isChecked)
                        onCheckedCallback?.onChecked(
                            position,
                            isChecked,
                            isAllChecked(),
                            multiCheckedList.size,
                            canCheckedItemCount()
                        )
                        true
                    } else {
                        //超过最大选择数
                        onMaxSelectCallback?.onSelectOverMax(maxCheckedNum)
                        false
                    }
                } else {
                    multiCheckedList.remove(item)
                    notifyItemChanged(position, isChecked)
                    onCheckedCallback?.onChecked(
                        position,
                        isChecked,
                        isAllChecked(),
                        multiCheckedList.size,
                        canCheckedItemCount()
                    )
                    return true
                }
            }
            else -> {
                return false
            }
        }
    }

    /**
     * 判断是否全选
     */
    open fun isAllChecked(): Boolean {
        if (checkedMode == CheckedMode.SINGLE) {
            return canCheckedItemCount() == 1 && singleCheckedItem != null
        }
        return multiCheckedList.size == canCheckedItemCount()
    }

    /**
     * 返回可选择条目总数
     */
    private fun canCheckedItemCount(): Int {
        return snapshot().count { it is CheckedData }
    }


    /**
     * 判断条目是否被选中
     */
    open fun itemIsChecked(position: Int): Boolean {
        val item = getData(position)
        return when (checkedMode) {
            CheckedMode.MULTI -> multiCheckedList.contains(item)
            CheckedMode.SINGLE -> singleCheckedItem == item
            CheckedMode.NONE -> false
        }
    }

    /**
     * 获取所有已选择条目
     */
    open fun getCheckedItemList(): List<DifferData> {
        if (isSingleCheckedMode()) {
            val checkData = singleCheckedItem
            return if (checkData == null) {
                emptyList()
            } else {
                listOf(checkData)
            }
        }
        return multiCheckedList.toList()
    }

    /**
     * 获取已选择数量
     */
    open fun getCheckedCount(): Int {
        return getCheckedItemList().size
    }

    /**
     * 获取单选索引
     */
    open fun getSingleCheckedItem() = singleCheckedItem

    /**
     * 条目被删除，同时删除选中索引
     */
    override fun removeItem(position: Int) {
        super.removeItem(position)
        multiCheckedList.remove(getData(position))
    }

    override fun removeItem(item: DifferData) {
        super.removeItem(item)
        multiCheckedList.remove(item)
    }

}
