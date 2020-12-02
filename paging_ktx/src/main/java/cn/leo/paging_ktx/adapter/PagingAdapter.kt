package cn.leo.paging_ktx.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.paging.filter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @author : leo
 * @date : 2020/5/11
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
abstract class PagingAdapter<T : Any> : PagingDataAdapter<T, RecyclerView.ViewHolder> {

    constructor() : super(itemCallback())

    constructor(diffCallback: DiffUtil.ItemCallback<T>) : super(diffCallback)

    companion object {
        fun <T> itemCallback(
            areItemsTheSame: (T, T) -> Boolean = { o, n -> o == n },
            areContentsTheSame: (T, T) -> Boolean = { o, n -> o == n },
            getChangePayload: (T, T) -> Any? = { _, _ -> null }
        ): DiffUtil.ItemCallback<T> {
            return object : DiffUtil.ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    return areItemsTheSame(oldItem, newItem)
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    return areContentsTheSame(oldItem, newItem)
                }

                override fun getChangePayload(oldItem: T, newItem: T): Any? {
                    return getChangePayload(oldItem, newItem)
                }
            }
        }
    }
    //<editor-fold desc="子类必须实现">
    /**
     * 获取条目类型的布局
     *
     * @param position 索引
     * @return 布局id
     */
    @LayoutRes
    protected abstract fun getItemLayout(position: Int): Int

    /**
     * 给条目绑定数据
     *
     * @param item  条目帮助类
     * @param data    对应数据
     * @param payloads item局部变更
     */
    protected abstract fun bindData(item: ItemHelper, data: T?, payloads: MutableList<Any>? = null)

    //</editor-fold>

    //<editor-fold desc="父类方法实现">
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? PagingAdapter<*>.ViewHolder)?.onBindViewHolder(position)
    }

    override fun getItemViewType(position: Int): Int {
        return getItemLayout(position)
    }

    fun getData(position: Int): T? {
        return getItem(position)
    }
    //</editor-fold>

    //<editor-fold desc="数据处理">
    /**
     * 保存提交的数据集
     */
    protected lateinit var mPagingData: PagingData<T>

    /**
     * 协程
     */
    protected lateinit var mScope: CoroutineScope

    /**
     * 采用setPagingData 可以动态增减数据
     */
    open fun setPagingData(scope: CoroutineScope, pagingData: PagingData<T>) {
        mScope = scope
        mPagingData = pagingData
        submitPagingData()
    }

    /**
     * 提交数据
     */
    private fun submitPagingData() {
        mScope.launch {
            submitData(mPagingData)
        }
    }

    /**
     * 向尾部添加数据
     */
    fun appendItem(item: T) {
        if (!this::mPagingData.isInitialized || !this::mScope.isInitialized) {
            throw IllegalArgumentException("To add data, you must use the 'setPagingData' method")
        }
        mPagingData = mPagingData.insertFooterItem(item)
        submitPagingData()
    }

    /**
     * 向首部添加数据
     */
    fun prependItem(item: T) {
        if (!this::mPagingData.isInitialized || !this::mScope.isInitialized) {
            throw IllegalArgumentException("To add data, you must use the 'setPagingData' method")
        }
        mPagingData = mPagingData.insertHeaderItem(item)
        submitPagingData()
    }

    /**
     * 过滤数据
     * @param predicate 条件为false的移除，为true的保留
     */
    fun filterItem(predicate: suspend (T) -> Boolean) {
        if (!this::mPagingData.isInitialized || !this::mScope.isInitialized) {
            throw IllegalArgumentException("To edit data, you must use the 'setPagingData' method")
        }
        mPagingData = mPagingData.filter(predicate)
        submitPagingData()
    }

    /**
     * 移除数据
     * @param item 要移除的条目
     */
    fun removeItem(item: T) {
        filterItem { it != item }
    }

    /**
     * 移除数据
     * @param position 要移除的条目的索引
     */
    fun removeItem(position: Int) {
        filterItem { it != getData(position) }
    }

    /**
     * 修改条目内容
     * @param position 条目索引
     * @param payload 局部刷新
     */
    fun edit(@IntRange(from = 0) position: Int, payload: Any? = null, block: (T?) -> Unit = {}) {
        if (position >= itemCount) {
            return
        }
        block(getData(position))
        notifyItemChanged(position, payload)
    }

    //</editor-fold>
    /**
     * 局部刷新
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val viewHolder = holder as? PagingAdapter<*>.ViewHolder
            val helper = viewHolder?.itemHelper
            val itemHolder = helper?.mItemHolder
            val item = getItem(position)
            if (itemHolder != null) {
                itemHolder.bindData(helper, item, payloads)
            } else {
                (holder as? PagingAdapter<*>.ViewHolder)?.onBindViewHolder(position, payloads)
            }
        }
    }


    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        val viewHolder = holder as? PagingAdapter<*>.ViewHolder
        val helper = viewHolder?.itemHelper
        val itemHolder = helper?.mItemHolder
        itemHolder?.onViewDetach(helper)
    }

    inner class ViewHolder internal constructor(parent: ViewGroup, layout: Int) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        ),
        View.OnClickListener,
        View.OnLongClickListener {
        val itemHelper: ItemHelper = ItemHelper(this)

        init {
            itemHelper.setLayoutResId(layout)
            itemHelper.setOnItemChildClickListener(mOnItemChildClickListenerProxy)
            itemHelper.setOnItemChildLongClickListener(mOnItemChildLongClickListenerProxy)
            itemHelper.setRVAdapter(this@PagingAdapter)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        val mPosition: Int
            get() = if (bindingAdapterPosition == -1) {
                bindPosition
            } else {
                bindingAdapterPosition
            }

        //传进来的position值，在手动创建holder时候bindingAdapterPosition为-1，需要使用传进来的值
        var bindPosition: Int = 0

        fun onBindViewHolder(position: Int, payloads: MutableList<Any>? = null) {
            bindPosition = position
            bindData(itemHelper, getItem(position), payloads)
        }

        override fun onClick(v: View) {
            if (::mOnItemClickListener.isInitialized) {
                mOnItemClickListener(this@PagingAdapter, v, mPosition)
            }
        }

        override fun onLongClick(v: View): Boolean {
            if (::mOnItemLongClickListener.isInitialized) {
                mOnItemLongClickListener(this@PagingAdapter, v, mPosition)
                return true
            }
            return false
        }
    }

    //<editor-fold desc="事件监听">
    private lateinit var mOnItemClickListener:
                (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemLongClickListener:
                (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildClickListener:
                (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    private lateinit var mOnItemChildLongClickListener:
                (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    val mOnItemChildClickListenerProxy:
                (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit =
        { adapter, v, position ->
            if (::mOnItemChildClickListener.isInitialized) {
                mOnItemChildClickListener(adapter, v, position)
            }
        }
    val mOnItemChildLongClickListenerProxy:
                (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit =
        { adapter, v, position ->
            if (::mOnItemChildLongClickListener.isInitialized) {
                mOnItemChildLongClickListener(adapter, v, position)
            }
        }

    fun setOnItemClickListener(
        onItemClickListener:
            (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(
        onItemLongClickListener:
            (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemLongClickListener = onItemLongClickListener
    }

    fun setOnItemChildClickListener(
        onItemChildClickListener:
            (adapter: PagingAdapter<out Any>, v: View, position: Int) -> Unit
    ) {
        mOnItemChildClickListener = onItemChildClickListener
    }
    //</editor-fold>

    //<editor-fold desc="状态监听">

    /**
     * 刷新状态监听
     */
    private val mOnRefreshStateListenerArray
            by lazy { ArrayList<(State) -> Unit>() }

    /**
     * 向后加载更多状态监听
     */
    private val mOnLoadMoreStateListenerArray
            by lazy { ArrayList<(State) -> Unit>() }

    /**
     * 向前加载更多监听
     */
    private val mOnPrependStateListenerArray
            by lazy { ArrayList<(State) -> Unit>() }

    init {
        addLoadStateListener {
            dispatchState(
                it.refresh,
                it.source.append.endOfPaginationReached,
                mOnRefreshStateListenerArray
            )
            dispatchState(
                it.append,
                it.source.append.endOfPaginationReached,
                mOnLoadMoreStateListenerArray
            )
            dispatchState(
                it.prepend,
                it.source.append.endOfPaginationReached,
                mOnPrependStateListenerArray
            )
        }
    }

    private fun dispatchState(
        loadState: LoadState,
        noMoreData: Boolean,
        stateListener: ArrayList<(State) -> Unit>
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                observer(State.Loading, stateListener)
            }
            is LoadState.NotLoading -> {
                observer(State.Success(noMoreData), stateListener)
            }
            is LoadState.Error -> {
                observer(State.Error, stateListener)
            }
        }
    }

    /**
     * 通知给所有订阅者
     */
    private fun observer(state: State, stateListener: ArrayList<(State) -> Unit>) {
        stateListener.forEach { it(state) }
    }

    /**
     * 刷新状态监听
     */
    fun addOnRefreshStateListener(listener: (State) -> Unit) {
        mOnRefreshStateListenerArray += listener
    }

    /**
     * 向后加载更多状态监听
     */
    fun addOnLoadMoreStateListener(listener: (State) -> Unit) {
        mOnLoadMoreStateListenerArray += listener
    }

    /**
     * 向前加载更多状态监听
     */
    fun addOnPrependStateListener(listener: (State) -> Unit) {
        mOnPrependStateListenerArray += listener
    }

    //</editor-fold>
}