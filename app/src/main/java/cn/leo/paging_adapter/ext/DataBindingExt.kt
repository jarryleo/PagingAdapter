package cn.leo.paging_adapter.ext

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import cn.leo.paging_ktx.adapter.ItemHelper

/**
 * @author : leo
 * @date : 2020/11/18
 * @description : DataBinding 拓展
 */


/**
 * RecyclerView 列表条目绑定，PagingAdapter库定制拓展
 */
const val BINDING_KEY = "DataBinding_Key"
inline fun <reified T : ViewDataBinding> ItemHelper.binding(): T? {
    var dataBinding = getTag(BINDING_KEY) as? T
    if (dataBinding == null) {
        dataBinding = DataBindingUtil.bind<T>(itemView)?.apply {
            setTag(BINDING_KEY, this)
        }
    }
    return dataBinding
}

/**
 * activity 绑定
 */
inline fun <reified T : ViewDataBinding> Activity.binding(@LayoutRes resId: Int): Lazy<T> =
    lazy { DataBindingUtil.setContentView<T>(this, resId) }

/**
 * fragment 绑定
 */
inline fun <reified T : ViewDataBinding> Fragment.binding(
    inflater: LayoutInflater,
    @LayoutRes resId: Int,
    container: ViewGroup?
): T = DataBindingUtil.inflate(inflater, resId, container, false)