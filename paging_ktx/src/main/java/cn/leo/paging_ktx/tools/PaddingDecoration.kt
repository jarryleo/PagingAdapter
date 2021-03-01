package cn.leo.paging_ktx.tools

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 *  @author : leo
 *  RecyclerView 间距调整，不是分割线，只是间距
 */
@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class PaddingDecoration : RecyclerView.ItemDecoration {
    //条目之间的间距，不包含边缘
    var leftSpace: Int = 0
    var rightSpace: Int = 0
    var topSpace: Int = 0
    var bottomSpace: Int = 0

    //条目到 RecyclerView 边缘的间距
    var leftSide: Int = 0
    var rightSide: Int = 0
    var topSide: Int = 0
    var bottomSide: Int = 0

    constructor(space: Int = 0) {
        this.leftSpace = space
        this.rightSpace = space
        this.topSpace = space
        this.bottomSpace = space
    }

    constructor(leftSpace: Int = 0, rightSpace: Int = 0, topSpace: Int = 0, bottomSpace: Int = 0) {
        this.leftSpace = leftSpace
        this.rightSpace = rightSpace
        this.topSpace = topSpace
        this.bottomSpace = bottomSpace
    }

    constructor(
        leftSpace: Int = 0, rightSpace: Int = 0, topSpace: Int = 0, bottomSpace: Int = 0,
        leftSide: Int = 0, rightSide: Int = 0, topSide: Int = 0, bottomSide: Int = 0
    ) {
        this.leftSpace = leftSpace
        this.rightSpace = rightSpace
        this.topSpace = topSpace
        this.bottomSpace = bottomSpace
        this.leftSide = leftSide
        this.rightSide = rightSide
        this.topSide = topSide
        this.bottomSide = bottomSide
    }


    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        when (val layoutManager = parent.layoutManager) {
            //表格布局
            is GridLayoutManager -> {
                val vertical = layoutManager.orientation == GridLayoutManager.VERTICAL
                val spanCount = layoutManager.spanCount
                val itemCount = layoutManager.itemCount
                val position = parent.getChildAdapterPosition(view)
                val spanSizeLookup = layoutManager.spanSizeLookup
                if (spanSizeLookup is GridLayoutManager.DefaultSpanSizeLookup) {
                    //每行的条目数相等处理
                    setRect(outRect, spanCount, itemCount, position, vertical)
                } else {
                    //每行数量不等处理 (跨列的条目，多少列算多少个) ，原理是假设当前条目之前的所有条目都是不跨列的
                    //总条目数
                    val virtualItemSpanCount =
                        (0 until itemCount).sumBy { spanSizeLookup.getSpanSize(it) }
                    //虚拟指针(跨列的条目，多少列算多少个)，原理是假设当前条目之前的所有条目都是不跨列的
                    val virtualPosition =
                        (0 until position).sumBy { spanSizeLookup.getSpanSize(it) }
                    //当前条目在它所在行处于第几列
                    val column = virtualPosition % spanCount
                    //当前view 所占列加它左边的列数
                    val leftColumn = column + spanSizeLookup.getSpanSize(position)
                    //虚拟列数(当前position所在行，具体有多少列，有跨列算一列)
                    val virtualSpanCount = if (leftColumn == spanCount) {
                        column + 1 //当前view 和它左侧view 满一行 则虚拟列为 左侧列 + 1
                    } else {
                        spanCount //否则为真实列数
                    }
                    setRect(
                        outRect,
                        virtualSpanCount,
                        virtualItemSpanCount,
                        virtualPosition,
                        vertical
                    )
                }
            }
            //瀑布流布局
            is StaggeredGridLayoutManager -> {
                val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
                val vertical = layoutManager.orientation == StaggeredGridLayoutManager.VERTICAL
                val spanCount = layoutManager.spanCount
                val itemCount = layoutManager.itemCount
                val position = lp.spanIndex
                setRect(outRect, spanCount, itemCount, position, vertical)
            }
            //线性布局
            is LinearLayoutManager -> {
                val vertical = layoutManager.orientation == LinearLayoutManager.VERTICAL
                val spanCount = 1
                val itemCount = layoutManager.itemCount
                val position = parent.getChildAdapterPosition(view)
                setRect(outRect, spanCount, itemCount, position, vertical)
            }
            //其它，不管边缘，只管间距
            else -> {
                outRect.left = leftSpace
                outRect.bottom = bottomSpace
                outRect.top = topSpace
                outRect.right = rightSpace
            }
        }

    }

    private fun setRect(
        outRect: Rect,
        spanCount: Int,
        totalCount: Int,
        position: Int,
        isVertical: Boolean
    ) {
        //总行数
        val totalRow = totalCount / spanCount + if (totalCount % spanCount == 0) {
            0
        } else {
            1
        }
        //行
        val row = position / spanCount
        //列
        val column = position % spanCount

        outRect.left = if (isVertical) {
            if (column == 0) {
                leftSide
            } else {
                leftSpace
            }
        } else {
            if (row == 0) {
                leftSide
            } else {
                leftSpace
            }
        }

        outRect.right = if (isVertical) {
            if (column == spanCount - 1) {
                rightSide
            } else {
                rightSpace
            }
        } else {
            if (row == totalRow - 1) {
                rightSide
            } else {
                rightSpace
            }
        }

        outRect.top = if (isVertical) {
            if (row == 0) {
                topSide
            } else {
                topSpace
            }
        } else {
            if (column == spanCount - 1) {
                topSide
            } else {
                topSpace
            }
        }

        outRect.bottom = if (isVertical) {
            if (row == totalRow - 1) {
                bottomSide
            } else {
                bottomSpace
            }
        } else {
            if (column == 0) {
                bottomSide
            } else {
                bottomSpace
            }
        }

    }

}