package cn.leo.paging_ktx

/**
 * @author : leo
 * @date : 2020/12/2
 * @description : 条目holder
 */
abstract class ItemHolder<T : Any> {

    /**
     * 绑定数据
     *
     * @param helper 帮助类
     * @param data   数据
     */
    abstract fun bindData(helper: ItemHelper, data: T?, payloads: MutableList<Any>? = null)

    /**
     * 初始化view，只在view第一次创建调用
     *
     * @param helper 帮助类
     * @param data   数据
     */
    open fun initView(helper: ItemHelper, data: T?) {}


    /**
     * 被回收时调用，用来释放一些资源，或者重置数据等
     *
     * @param helper 帮助类
     */
    open fun onViewDetach(helper: ItemHelper) {}
}