 package cn.leo.paging_adapter.net.view_model

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @author : leo
 * @date : 2019-11-15
 */
class ViewModelCreator<T : ViewModel>(private val clazz: Class<T>) :
    ReadOnlyProperty<ViewModelStoreOwner, T> {
    override fun getValue(thisRef: ViewModelStoreOwner, property: KProperty<*>): T {
        /**
         * 绑定ViewModelStoreOwner 在 view层销毁时候会通知所有 ViewModel 调用 clear() 方法
         * 用户需重写 ViewModel 的 onCleared() 来执行回收操作
         */
        return ViewModelProvider(thisRef).get(clazz)
    }
}

/**
 * Fragment共享model 所有和这个fragment处于同一个activity的
 * fragment 以及 activity 获取到的同名model都是同一个对象，可以订阅同一个方法，同时拿到回调
 */
class ShareModelCreator<T : ViewModel>(private val clazz: Class<T>) :
    ReadOnlyProperty<Fragment, T> {
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return ViewModelProvider(thisRef.requireActivity()).get(clazz)
    }
}

