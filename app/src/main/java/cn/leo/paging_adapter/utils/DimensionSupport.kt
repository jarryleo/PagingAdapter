package cn.leo.paging_adapter.utils

import android.content.res.Resources
import kotlin.math.roundToInt

/**
 * @author : leo
 * @date : 2019-11-29
 */

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
val Double.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
val Float.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()


/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
val Double.px: Int get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()
val Float.px: Int get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()
val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()
