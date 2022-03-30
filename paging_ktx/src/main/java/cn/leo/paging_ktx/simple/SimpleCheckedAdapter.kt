package cn.leo.paging_ktx.simple

import androidx.annotation.IntRange

/**
 * @author : ling luo
 * @date : 2022/3/30
 * @description : 可选择的 adapter
 *
 * 需要解决分页加载 和全选冲突问题
 *
 */
open class SimpleCheckedAdapter : SimplePagingAdapter() {
    enum class CheckedModel {
        NONE, SINGLE, MULTI  //选择模式，无，单选/多选
    }

    private var checkedModel = CheckedModel.NONE  //当前选择模式

    private var singleCheckIndex = -1 //单选索引

    private var multiCheckIndexList: MutableSet<Int> = mutableSetOf() //多选索引

    private var maxCheckedNum = Int.MAX_VALUE  //多选限制，最多选择多少个


    /**
     * 更新所有条目
     */
    private fun notifyAllItem() {
        notifyItemRangeChanged(0, itemCount - 1)
    }

    /**
     * 设置选择模式
     */
    private fun setCheckModel(model: CheckedModel) {
        checkedModel = model
        notifyAllItem()
    }

    /**
     * 关闭选择模式
     */
    open fun closeCheckModel() {
        setCheckModel(CheckedModel.NONE)
    }

    /**
     * 设置单选模式
     */
    open fun setSingleCheckModel() {
        setCheckModel(CheckedModel.SINGLE)
    }

    /**
     * 设置多选模式
     */
    open fun setMultiCheckModel() {
        setCheckModel(CheckedModel.MULTI)
    }

    /**
     * 是否是单选模式
     */
    open fun isSingleCheckedModel(): Boolean {
        return checkedModel == CheckedModel.SINGLE
    }

    /**
     * 是否是多选模式
     */
    open fun isMultiCheckedModel(): Boolean {
        return checkedModel == CheckedModel.MULTI
    }

    /**
     * 设置最大选择数
     */
    open fun setMaxChecked(@IntRange(from = 0L) max: Int) {
        maxCheckedNum = max
        if (multiCheckIndexList.size > max) {
            cancelChecked()
        }
    }


    /**
     * 反选
     */
    open fun reverseChecked() {
        if (checkedModel != CheckedModel.MULTI) return
        val all = (0 until itemCount).toMutableSet()
        all.removeAll(multiCheckIndexList)
        multiCheckIndexList = all
        notifyAllItem()
    }

    /**
     * 全选
     */
    open fun checkedAll() {
        if (checkedModel != CheckedModel.MULTI) return
        multiCheckIndexList = (0 until itemCount).toMutableSet()
        notifyAllItem()
    }

    /**
     * 取消选择
     */
    open fun cancelChecked() {
        singleCheckIndex = -1
        multiCheckIndexList = mutableSetOf()
        notifyAllItem()
    }

    /**
     * 设置条目选择状态
     * @param position 条目索引
     * @param isChecked 是否选择
     *
     */
    open fun setChecked(position: Int, isChecked: Boolean) {
        when (checkedModel) {
            CheckedModel.SINGLE -> {
                singleCheckIndex = if (isChecked) {
                    position
                } else {
                    -1
                }
            }
            CheckedModel.MULTI -> {
                if (isChecked) {
                    if (multiCheckIndexList.size < maxCheckedNum) {
                        multiCheckIndexList.add(position)
                    } else {
                        //通知超过最大选择数 todo
                    }
                } else {
                    multiCheckIndexList.remove(position)
                }

            }
            else -> {}
        }
    }

    /**
     * 判断是否全选，实际要根据holder性质和分页加载数量判断
     */
    open fun isAllChecked(): Boolean {
        return multiCheckIndexList.size == itemCount
    }

    /**
     * 判断条目是否被选中
     */
    open fun itemIsChecked(position: Int): Boolean {
        return when (checkedModel) {
            CheckedModel.MULTI -> multiCheckIndexList.contains(position)
            CheckedModel.SINGLE -> singleCheckIndex == position
            CheckedModel.NONE -> false
        }
    }
}