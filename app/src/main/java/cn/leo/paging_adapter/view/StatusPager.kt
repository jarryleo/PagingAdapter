package cn.leo.paging_adapter.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import java.util.*

/**
 * @author : leo
 * @date : 2019/4/18
 */

@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
class StatusPager private constructor(private val mBuilder: Builder) :
    View.OnClickListener {

    companion object {
        /**
         * 传入要替换展示状态的view
         */
        fun builder(replaceView: View): Builder {
            return Builder(replaceView)
        }

        const val VIEW_STATE_CONTENT = 0
        const val VIEW_STATE_ERROR = 1
        const val VIEW_STATE_EMPTY = 2
        const val VIEW_STATE_LOADING = 3
        const val VIEW_STATE_CUSTOM = 4
    }

    /**
     * 当前状态
     *
     * @return
     */
    @ViewState
    var curState = VIEW_STATE_CONTENT
        private set

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        VIEW_STATE_CONTENT,
        VIEW_STATE_ERROR,
        VIEW_STATE_EMPTY,
        VIEW_STATE_LOADING,
        VIEW_STATE_CUSTOM
    )
    annotation class ViewState
    class Builder(var mTarget: View) {
        val mContext: Context = mTarget.context
        var mReplace: View? = null
        var mLoadingId = View.NO_ID
        var mEmptyId = View.NO_ID
        var mErrorId = View.NO_ID
        var mClickIds: MutableList<Int>? = null
        var mOnClickListener: (StatusPager, View) -> Unit = { _, _ -> }
        var mIsRelative = false

        /**
         * 替代view位置展示不同状态page
         * 如果view 的父view 是
         */
        private fun initSuccessView() {
            var parent = mTarget.parent as? ViewGroup
            mIsRelative = parent is RelativeLayout ||
                    parent is ConstraintLayout ||
                    parent is ViewPager
            if (parent == null) {
                if (mTarget is ViewGroup) {
                    parent = mTarget as ViewGroup
                    mTarget = parent.getChildAt(0)
                } else {
                    return
                }
            }
            if (!mIsRelative) {
                val frameLayout = FrameLayout(mContext)
                frameLayout.layoutParams = mTarget.layoutParams
                val index = parent.indexOfChild(mTarget)
                parent.removeView(mTarget)
                val lp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                frameLayout.addView(mTarget, lp)
                parent.addView(frameLayout, index)
            }
        }

        /**
         * 加载页布局资源id
         */
        fun loadingViewLayout(@LayoutRes layoutRes: Int): Builder {
            mLoadingId = layoutRes
            return this
        }

        /**
         * 空页面布局资源id
         */
        fun emptyViewLayout(@LayoutRes layoutRes: Int): Builder {
            mEmptyId = layoutRes
            return this
        }

        /**
         * 错误页面布局资源id
         */
        fun errorViewLayout(@LayoutRes layoutRes: Int): Builder {
            mErrorId = layoutRes
            return this
        }

        /**
         * 重试按钮id
         */
        fun addRetryButtonId(@IdRes id: Int): Builder {
            if (mClickIds == null) {
                mClickIds = ArrayList()
            }
            mClickIds?.add(id)
            return this
        }

        fun setRetryClickListener(listener: (StatusPager, View) -> Unit = { _, _ -> }): Builder {
            mOnClickListener = listener
            return this
        }

        fun build(): StatusPager {
            return StatusPager(this)
        }

        init {
            initSuccessView()
        }
    }

    fun setRetryClickListener(listener: (StatusPager, View) -> Unit = { _, _ -> }): StatusPager {
        mBuilder.mOnClickListener = listener
        return this
    }

    fun showLoading(): ViewHelper {
        if (mBuilder.mLoadingId == View.NO_ID) {
            throw NullPointerException("loading layout is invalid")
        }
        replaceView(mBuilder.mLoadingId)
        setClick()
        curState = VIEW_STATE_LOADING
        return ViewHelper(mBuilder.mReplace)
    }

    fun showEmpty(): ViewHelper {
        if (mBuilder.mEmptyId == View.NO_ID) {
            throw NullPointerException("empty layout is invalid")
        }
        replaceView(mBuilder.mEmptyId)
        setClick()
        curState = VIEW_STATE_EMPTY
        return ViewHelper(mBuilder.mReplace)
    }

    fun showError(): ViewHelper {
        if (mBuilder.mErrorId == View.NO_ID) {
            throw NullPointerException("error layout is invalid")
        }
        replaceView(mBuilder.mErrorId)
        setClick()
        curState = VIEW_STATE_ERROR
        return ViewHelper(mBuilder.mReplace)
    }

    fun showContent() {
        mBuilder.mTarget.visibility = View.VISIBLE
        if (mBuilder.mReplace != null) {
            mBuilder.mReplace?.visibility = View.GONE
            val parent = mBuilder.mTarget.parent as ViewGroup
            parent.removeView(mBuilder.mReplace)
            mBuilder.mReplace = null
        }
        curState = VIEW_STATE_CONTENT
    }

    fun showCustom(@LayoutRes layoutRes: Int): ViewHelper {
        if (layoutRes == View.NO_ID) {
            throw NullPointerException("error layout is invalid")
        }
        replaceView(layoutRes)
        setClick()
        curState = VIEW_STATE_CUSTOM
        return ViewHelper(mBuilder.mReplace)
    }

    /**
     * 加载页布局资源id
     */
    fun loadingViewLayout(@LayoutRes layoutRes: Int): StatusPager {
        mBuilder.loadingViewLayout(layoutRes)
        return this
    }

    /**
     * 空页面布局资源id
     */
    fun emptyViewLayout(@LayoutRes layoutRes: Int): StatusPager {
        mBuilder.emptyViewLayout(layoutRes)
        return this
    }

    /**
     * 错误页面布局资源id
     */
    fun errorViewLayout(@LayoutRes layoutRes: Int): StatusPager {
        mBuilder.errorViewLayout(layoutRes)
        return this
    }

    private fun setClick() {
        if (mBuilder.mClickIds == null) {
            return
        }
        for (clickId in mBuilder.mClickIds!!) {
            val clickView = getViewById(mBuilder.mReplace!!, clickId)
            clickView?.setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        mBuilder.mOnClickListener(this, v)
    }

    private fun getViewById(
        view: View,
        @IdRes viewId: Int
    ): View? {
        return if (view.id == viewId) view else view.findViewById(viewId)
    }

    private fun replaceView(layoutRes: Int) {
        if (mBuilder.mReplace != null) {
            val id = mBuilder.mReplace?.id
            if (id == layoutRes) {
                mBuilder.mReplace?.visibility = View.VISIBLE
                return
            }
        }
        val parent = mBuilder.mTarget.parent as ViewGroup
        parent.removeView(mBuilder.mReplace)
        val inflate =
            LayoutInflater.from(mBuilder.mContext).inflate(layoutRes, null)
        val params = mBuilder.mTarget.layoutParams
        inflate.layoutParams = params
        inflate.id = layoutRes
        mBuilder.mReplace = inflate
        val index = parent.indexOfChild(mBuilder.mTarget)
        parent.addView(mBuilder.mReplace, index)
        if (mBuilder.mIsRelative) {
            mBuilder.mTarget.visibility = View.INVISIBLE
        } else {
            mBuilder.mTarget.visibility = View.GONE
        }
        mBuilder.mReplace?.visibility = View.VISIBLE
    }

    class ViewHelper(private val mView: View?) {
        fun <V : View> findViewById(@IdRes viewId: Int): V {
            if (mView == null) {
                throw NullPointerException("view is null")
            }
            val view: V? = mView.findViewById(viewId)
            if (view == null) {
                val entryName = mView.resources.getResourceEntryName(viewId)
                throw NullPointerException("id: R.id.$entryName can not find in this item!")
            }
            return view
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param text   设置的文字
         */
        fun setText(
            @IdRes viewId: Int,
            text: CharSequence?
        ): ViewHelper {
            val view = findViewById<View>(viewId)
            if (view is TextView) {
                view.text = text
            } else {
                val entryName = view.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
            return this
        }

        /**
         * 给按钮或文本框设置文字
         *
         * @param viewId 控件id
         * @param resId  设置的文字资源
         */
        fun setText(
            @IdRes viewId: Int,
            @StringRes resId: Int
        ): ViewHelper {
            val view = findViewById<View>(viewId)
            if (view is TextView) {
                view.setText(resId)
            } else {
                val entryName = view.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not TextView")
            }
            return this
        }

        /**
         * 给图片控件设置资源图片
         *
         * @param viewId 图片控件id
         * @param resId  资源id
         */
        fun setImageResource(
            @IdRes viewId: Int,
            @DrawableRes resId: Int
        ): ViewHelper {
            val view = findViewById<View>(viewId)
            if (view is ImageView) {
                view.setImageResource(resId)
            } else {
                val entryName = view.resources.getResourceEntryName(viewId)
                throw ClassCastException("id: R.id.$entryName are not ImageView")
            }
            return this
        }

        /**
         * 设置view的背景
         *
         * @param viewId 控件id
         * @param resId  资源id
         */
        fun setBackgroundResource(
            @IdRes viewId: Int,
            @DrawableRes resId: Int
        ): ViewHelper {
            val view = findViewById<View>(viewId)
            view.setBackgroundResource(resId)
            return this
        }

        fun setVisibility(@IdRes viewId: Int, visibility: Int): ViewHelper {
            val view = findViewById<View>(viewId)
            view.visibility = visibility
            return this
        }

        fun setViewVisible(@IdRes viewId: Int): ViewHelper {
            val view = findViewById<View>(viewId)
            view.visibility = View.VISIBLE
            return this
        }

        fun setViewInvisible(@IdRes viewId: Int): ViewHelper {
            val view = findViewById<View>(viewId)
            view.visibility = View.INVISIBLE
            return this
        }

        fun setViewGone(@IdRes viewId: Int): ViewHelper {
            val view = findViewById<View>(viewId)
            view.visibility = View.GONE
            return this
        }

        fun setOnClickListener(
            @IdRes viewId: Int,
            onClickListener: View.OnClickListener?
        ): ViewHelper {
            val view = findViewById<View>(viewId)
            view.setOnClickListener(onClickListener)
            return this
        }

    }
}