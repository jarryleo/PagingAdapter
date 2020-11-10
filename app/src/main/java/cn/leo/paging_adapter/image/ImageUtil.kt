package cn.leo.paging_adapter.image

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import cn.leo.paging_adapter.image.glide.CircleBorderTransform
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import java.io.File

/**
 * @author : leo
 * @date : 2019-08-31
 * @desc glide 加载图片封装
 */

fun ImageView.loadImage(
    url: String? = null,
    uri: Uri? = null,
    @RawRes @DrawableRes
    resId: Int = -1,
    file: File? = null,
    bitmap: Bitmap? = null,
    thumbnail: String? = null,
    circle: Boolean = false,
    circleBorderWidth: Int = 0,
    @ColorInt
    circleBorderColor: Int = Color.WHITE,
    skipCache: Boolean = false,
    corners: Int = 0,
    @DrawableRes defResId: Int = -1,
    @DrawableRes errResId: Int = defResId,
    onLoadFailed: ((
        exception: GlideException?,
        isFirstResource: Boolean
    ) -> Unit)? = null,
    onLoadSuccess: ((
        resource: Drawable?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ) -> Unit)? = null

) {
    //页面泄漏处理
    if ((context as? Activity)?.isDestroyed == true) {
        return
    }
    //图片位置处理
    val transform =
        when (scaleType) {
            ImageView.ScaleType.FIT_CENTER -> FitCenter()
            ImageView.ScaleType.CENTER_INSIDE -> CenterInside()
            else -> CenterCrop()
        }
    //图片裁剪
    val transforms = when {
        circle -> {
            val circleTransform = if (circleBorderWidth > 0) {
                CircleBorderTransform(circleBorderWidth, circleBorderColor)
            } else {
                CircleCrop()
            }
            arrayOf(circleTransform, transform)
        }
        (corners > 0) -> arrayOf(RoundedCorners(corners), transform)
        else -> arrayOf(transform)
    }
    Glide.with(this)
        //资源加载途径
        .load(
            when {
                !url.isNullOrEmpty() -> url
                uri != null -> uri
                resId != -1 -> resId
                file != null -> file
                bitmap != null -> bitmap
                else -> null
            }
        )
        //缓存处理
        .skipMemoryCache(skipCache)
        .diskCacheStrategy(
            if (skipCache) {
                DiskCacheStrategy.NONE
            } else {
                DiskCacheStrategy.AUTOMATIC
            }
        )
        //图片裁剪
        .transform(*transforms)
        //图片加载动画
        .transition(withCrossFade())
        //缩略图(顶替占位图实现裁剪)
        .thumbnail(
            loadTransform(this, defResId, thumbnail, transforms)
        )
        //占位图
        //.placeholder(defResId)
        .error(loadTransform(this, errResId, transformation = transforms))
        //加载回调
        .listener(if (onLoadFailed == null && onLoadSuccess == null) null else object :
            RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                if (onLoadFailed != null) {
                    onLoadFailed(e, isFirstResource)
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (onLoadSuccess != null) {
                    onLoadSuccess(resource, dataSource, isFirstResource)
                }
                return false
            }

        })
        .into(this)
}

/**
 * 占位图处理
 */
private fun loadTransform(
    view: ImageView,
    @DrawableRes
    resId: Int = -1,
    url: String? = null,
    transformation: Array<BitmapTransformation>
): RequestBuilder<Drawable>? {
    return Glide.with(view)
        .load(
            when {
                !url.isNullOrEmpty() -> url
                resId != -1 -> resId
                else -> null
            }
        )
        .apply(RequestOptions().transform(*transformation))
}

/**
 * 获取bitmap
 */
fun Context.getBitmap(
    url: String,
    callback: (bitmap: Bitmap, width: Int, height: Int) -> Unit
) {
    Glide.with(this)
        .asBitmap()
        .load(url)
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                callback(resource, resource.width, resource.height)
            }

        })
}

/**
 * 下载图片文件
 */
fun Context.downloadImage(url: String): File? {
    return try {
        Glide.with(this)
            .asFile()
            .load(url)
            .submit()
            .get()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


