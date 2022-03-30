package cn.leo.paging_ktx.ext

import cn.leo.paging_ktx.adapter.ItemHelper
import cn.leo.paging_ktx.simple.SimpleCheckedAdapter

/**
 * @author : ling luo
 * @date : 2022/3/30
 * @description : helper 拓展
 *
 */


/**
 * 当前条目是否是单选模式
 */
fun ItemHelper.isSingleCheckedModel(): Boolean {
    if (adapter !is SimpleCheckedAdapter<*>) return false
    return (adapter as? SimpleCheckedAdapter<*>)?.isSingleCheckedModel() ?: false
}

/**
 * 当前条目是否是多选模式
 */
fun ItemHelper.isMultiCheckedModel(): Boolean {
    if (adapter !is SimpleCheckedAdapter<*>) return false
    return (adapter as? SimpleCheckedAdapter<*>)?.isMultiCheckedModel() ?: false
}

/**
 * 当前条目是否选中
 */
fun ItemHelper.isChecked(): Boolean {
    if (adapter !is SimpleCheckedAdapter<*>) return false
    return (adapter as? SimpleCheckedAdapter<*>)?.itemIsChecked(position) ?: false
}