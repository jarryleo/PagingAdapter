package cn.leo.paging_adapter.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import cn.leo.paging_adapter.net.utils.main

/**
 * @author : ling luo
 * @date : 2020/7/1
 */

fun Context.toast(msg: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    if (msg.isNullOrEmpty()) return
    main { Toast.makeText(this@toast, msg, duration).show() }
}

fun Context.toast(@StringRes msgRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    main { Toast.makeText(this@toast, msgRes, duration).show() }
}
