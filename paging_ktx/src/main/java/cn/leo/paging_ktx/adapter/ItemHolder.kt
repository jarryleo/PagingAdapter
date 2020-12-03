package cn.leo.paging_ktx.adapter

import android.view.View
import androidx.annotation.CallSuper
import kotlinx.android.extensions.LayoutContainer

/**
 * @author : leo
 * @date : 2020/12/2
 * @description : 条目holder
 */

@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class ItemHolder<T : Any> : LayoutContainer {

    protected var itemView: View? = null

    override val containerView: View?
        get() = itemView

    /**
     * 绑定数据
     *
     * @param item   帮助类
     * @param data   数据
     */
    abstract fun bindData(item: ItemHelper, data: T?, payloads: MutableList<Any>? = null)

    /**
     * 初始化view，只在view第一次创建调用
     *
     * @param item   帮助类
     * @param data   数据
     */
    @CallSuper
    open fun initView(item: ItemHelper, data: T?) {
        itemView = item.itemView
    }


    /**
     * 被回收时调用，用来释放一些资源，或者重置数据等
     *
     * @param item   帮助类
     */
    open fun onViewDetach(item: ItemHelper) {}


}