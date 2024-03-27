package cn.leo.paging_ktx.tools

import android.graphics.Canvas
import android.graphics.Rect
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.collection.ArrayMap
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener

/**
 * Created by Leo on 2018/6/4.
 * recyclerView 顶部悬浮条目（类似分组标题悬浮）
 */
class FloatDecoration(private vararg val mViewTypes: Int) : ItemDecoration() {
    private val mHeightCache =
        ArrayMap<Int, Int>()
    private val mTypeCache =
        ArrayMap<Int, Int>()
    private val mHolderCache =
        ArrayMap<Int, RecyclerView.ViewHolder?>()
    private var mFloatView: View? = null
    private var mFloatPosition = -1
    private var mFloatBottom = 0
    private var lastLayoutCount = 0
    private var hasInit = false
    private var mRecyclerViewPaddingLeft = 0
    private var mRecyclerViewPaddingRight = 0
    private var mRecyclerViewPaddingTop = 0
    private var mRecyclerViewPaddingBottom = 0
    private var mHeaderLeftMargin = 0
    private var mHeaderTopMargin = 0
    private var mHeaderRightMargin = 0
    private val mClipBounds = Rect()
    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.onDrawOver(c, parent, state)
        val layoutManager = parent.layoutManager ?: return
        var firstView = parent.findChildViewUnder(
            mClipBounds.left.toFloat(),
            mRecyclerViewPaddingTop + mHeaderTopMargin * 1f
        )
        if (firstView == null) firstView = layoutManager.getChildAt(0)
        var secondView =
            parent.findChildViewUnder(mClipBounds.left.toFloat(), mFloatBottom.toFloat())
        if (firstView == null) return
        if (secondView == null) return
        val adapter = parent.adapter
        val firstViewPosition = parent.getChildAdapterPosition(firstView)
        var secondViewPosition = parent.getChildAdapterPosition(secondView)
        for (i in secondViewPosition - 1 downTo firstViewPosition + 1) {
            val itemViewType = adapter?.getItemViewType(i) ?: continue
            if (isFloatHolder(itemViewType)) {
                val view = layoutManager.findViewByPosition(i)
                if (view?.left == firstView.left) {
                    secondView = view
                    secondViewPosition = i
                }
                break
            }
        }
        val firstItemType = adapter?.getItemViewType(firstViewPosition)
        val secondItemType = adapter?.getItemViewType(secondViewPosition)
        if (!hasInit) {
            touch(parent)
        }
        if (isFloatHolder(firstItemType)) {
            if (firstViewPosition != mFloatPosition || firstViewPosition == 0) {
                mFloatPosition = firstViewPosition
                mFloatView = getFloatView(parent, firstView)
            }
            var top = 0
            if (isFloatHolder(secondItemType)) {
                val floatView = mFloatView
                if (floatView == null || secondView == null) return
                top = secondView.top - floatView.height - mRecyclerViewPaddingTop
            }
            drawFloatView(mFloatView, c, top)
            return
        }
        if (isFloatHolder(secondItemType)) {
            if (mFloatPosition > firstViewPosition) {
                mFloatPosition = findPreFloatPosition(parent)
                mFloatView = getFloatView(parent, null)
            }
            val floatView = mFloatView
            if (floatView == null || secondView == null) return
            val top = secondView.top - secondView.height - mRecyclerViewPaddingTop
            drawFloatView(mFloatView, c, top)
            return
        }
        if (mFloatView == null || lastLayoutCount != layoutManager.childCount) {
            mFloatPosition = findPreFloatPosition(parent)
            mFloatView = getFloatView(parent, null)
        }
        lastLayoutCount = layoutManager.childCount
        drawFloatView(mFloatView, c, 0)
    }

    /**
     * 绘制悬浮条目
     */
    private fun drawFloatView(
        v: View?,
        c: Canvas,
        top: Int
    ) {
        if (v == null) {
            return
        }
        mClipBounds.top = mRecyclerViewPaddingTop + mHeaderTopMargin
        mClipBounds.bottom = top + mClipBounds.top + v.height
        c.save()
        c.clipRect(mClipBounds)
        c.translate(
            mRecyclerViewPaddingLeft + mHeaderLeftMargin * 1f,
            top + mRecyclerViewPaddingTop + mHeaderTopMargin * 1f
        )
        v.draw(c)
        c.restore()
    }

    /**
     * 处理悬浮条目触摸事件
     */
    private fun touch(parent: RecyclerView) {
        if (hasInit) {
            return
        }
        hasInit = true
        parent.addOnItemTouchListener(object : SimpleOnItemTouchListener() {
            private val mGestureDetectorCompat =
                GestureDetectorCompat(parent.context, MyGestureListener())

            override fun onInterceptTouchEvent(
                rv: RecyclerView,
                e: MotionEvent
            ): Boolean {
                return isContains(e)
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                isContains(e)
            }

            private fun isContains(e: MotionEvent): Boolean {
                var contains =
                    mClipBounds.contains(e.x.toInt(), e.y.toInt())
                if (contains) {
                    mGestureDetectorCompat.onTouchEvent(e)
                    mFloatView?.onTouchEvent(e)
                }
                val drawRect = Rect()
                parent.getDrawingRect(drawRect)
                drawRect.top = mClipBounds.bottom
                drawRect.left = mRecyclerViewPaddingLeft
                drawRect.right -= mRecyclerViewPaddingRight
                drawRect.bottom -= mRecyclerViewPaddingBottom
                contains = !drawRect.contains(e.x.toInt(), e.y.toInt())
                return contains
            }
        })
    }

    private inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (mClipBounds.contains(e.x.toInt(), e.y.toInt())) {
                childClick(
                    mFloatView,
                    e.x - mRecyclerViewPaddingLeft,
                    e.y - mRecyclerViewPaddingTop
                )
            }
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            if (mClipBounds.contains(e.x.toInt(), e.y.toInt())) {
                childLongClick(
                    mFloatView,
                    e.x - mRecyclerViewPaddingLeft,
                    e.y - mRecyclerViewPaddingTop
                )
            }
        }

        /**
         * 遍历容器和它的子view，传递点击事件
         */
        private fun childClick(
            v: View?,
            x: Float,
            y: Float
        ) {
            val rect = Rect()
            v?.getGlobalVisibleRect(rect)
            if (rect.contains(x.toInt(), y.toInt())) {
                v?.performClick()
            }
            if (v is ViewGroup) {
                val childCount = v.childCount
                for (i in 0 until childCount) {
                    val view = v.getChildAt(i)
                    childClick(view, x, y)
                }
            }
        }

        /**
         * 遍历容器和它的子view，传递长按事件
         */
        private fun childLongClick(
            v: View?,
            x: Float,
            y: Float
        ) {
            val rect = Rect()
            v?.getGlobalVisibleRect(rect)
            if (rect.contains(x.toInt(), y.toInt())) {
                v?.performLongClick()
            }
            if (v is ViewGroup) {
                val childCount = v.childCount
                for (i in 0 until childCount) {
                    val view = v.getChildAt(i)
                    childLongClick(view, x, y)
                }
            }
        }
    }

    /**
     * 判断条目类型是否需要悬浮
     */
    private fun isFloatHolder(type: Int?): Boolean {
        for (viewType in mViewTypes) {
            if (type == viewType) {
                return true
            }
        }
        return false
    }

    /**
     * 查找之前的悬浮标题position
     */
    private fun findPreFloatPosition(recyclerView: RecyclerView): Int {
        val adapter = recyclerView.adapter
        val firstVisibleView = recyclerView.layoutManager?.getChildAt(0) ?: return -1
        val childAdapterPosition = recyclerView.getChildAdapterPosition(firstVisibleView)
        for (i in childAdapterPosition downTo 0) {
            if (isFloatHolder(adapter?.getItemViewType(i))) {
                return i
            }
        }
        return -1
    }

    /**
     * 获取要悬浮的itemView
     */
    private fun getFloatView(parent: RecyclerView, view: View?): View? {
        if (mFloatPosition < 0) return null
        if (view != null && view.height > 0) {
            mHeightCache[mFloatPosition] = view.height
            mTypeCache[parent.adapter?.getItemViewType(mFloatPosition)] = view.height
        }
        return getHolder(parent)?.itemView
    }

    /**
     * 获取之前要悬浮的holder
     */
    private fun getHolder(recyclerView: RecyclerView): RecyclerView.ViewHolder? {
        val adapter = recyclerView.adapter
        val viewType = adapter?.getItemViewType(mFloatPosition)
        var holder = mHolderCache[viewType]
        if (holder == null) {
            holder =
                adapter?.createViewHolder(recyclerView, adapter.getItemViewType(mFloatPosition))
            mHolderCache[viewType] = holder
        }
        if (holder == null) return null
        adapter?.bindViewHolder(holder, mFloatPosition)
        layoutView(holder.itemView, recyclerView)
        return holder
    }

    /**
     * 测量悬浮布局
     */
    private fun layoutView(v: View, parent: RecyclerView) {
        var lp = v.layoutParams
        if (lp == null) {
            // 标签默认宽度占满parent
            lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            v.layoutParams = lp
        }

        // 对高度进行处理
        val heightMode = View.MeasureSpec.EXACTLY
        var height = mHeightCache[mFloatPosition]
        if (height == null) {
            height = mTypeCache[parent.adapter?.getItemViewType(mFloatPosition)]
        }
        var heightSize = height ?: mClipBounds.height()
        mRecyclerViewPaddingLeft = parent.paddingLeft
        mRecyclerViewPaddingRight = parent.paddingRight
        mRecyclerViewPaddingTop = parent.paddingTop
        mRecyclerViewPaddingBottom = parent.paddingBottom
        if (lp is MarginLayoutParams) {
            val mlp = lp
            mHeaderLeftMargin = mlp.leftMargin
            mHeaderTopMargin = mlp.topMargin
            mHeaderRightMargin = mlp.rightMargin
        }

        // 最大高度为RecyclerView的高度减去padding
        val maxHeight =
            parent.height - mRecyclerViewPaddingTop - mRecyclerViewPaddingBottom
        // 不能超过maxHeight
        heightSize = minOf(heightSize, maxHeight)

        // 因为标签默认宽度占满parent，所以宽度强制为RecyclerView的宽度减去padding
        var widthSize = parent.width - mRecyclerViewPaddingLeft -
                mRecyclerViewPaddingRight - mHeaderLeftMargin - mHeaderRightMargin
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            widthSize /= spanCount
        }
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            widthSize,
            View.MeasureSpec.EXACTLY
        )
        val heightSpec = View.MeasureSpec.makeMeasureSpec(heightSize, heightMode)
        // 强制测量
        v.measure(widthSpec, heightSpec)
        val left = mRecyclerViewPaddingLeft + mHeaderLeftMargin
        val right = v.measuredWidth + left
        val top = mRecyclerViewPaddingTop + mHeaderTopMargin
        val bottom = v.measuredHeight + top

        // 位置强制布局在顶部
        v.layout(left, top, right, bottom)
        mClipBounds.top = top
        mClipBounds.bottom = bottom
        mClipBounds.left = left
        mClipBounds.right = right
        mFloatBottom = bottom
    }

}