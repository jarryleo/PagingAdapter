package cn.leo.paging_adapter.image.glide

import android.graphics.*
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.math.max
import kotlin.math.min

/**
 * @author : leo
 * @date : 2019-12-30
 * @desc : "带外圆环的圆形图片"
 */
class CircleBorderTransform(private val borderWidth: Int, private val borderColor: Int) :
    BitmapTransformation() {
    private val ID = javaClass.name
    override fun transform(
        pool: BitmapPool,
        source: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap { // 目标直径
        val destMinEdge = min(outWidth, outHeight)
        // 目标半径 & 中心点坐标
        val radius = destMinEdge / 2f
        // 修正源宽高
        val srcWidth = source.width
        val srcHeight = source.height
        val scaleX = (destMinEdge - borderWidth * 2) / srcWidth.toFloat()
        val scaleY = (destMinEdge - borderWidth * 2) / srcHeight.toFloat()
        val maxScale = max(scaleX, scaleY)
        val scaledWidth = maxScale * srcWidth
        val scaledHeight = maxScale * srcHeight
        // 源绘制起始坐标
        val left = (destMinEdge - scaledWidth) / 2f
        val top = (destMinEdge - scaledHeight) / 2f
        // 新建画布
        val outBitmap = pool[destMinEdge, destMinEdge, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(outBitmap)
        // 绘制内圆
        val srcPaint =
            Paint(Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        srcPaint.color = borderColor
        val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
        canvas.drawCircle(radius, radius, radius - borderWidth, srcPaint)
        srcPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(source, null, destRect, srcPaint)
        // 绘制外圆
        val borderPaint =
            Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG)
        borderPaint.color = borderColor
        borderPaint.style = Paint.Style.FILL
        borderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
        canvas.drawCircle(radius, radius, radius, borderPaint)
        return outBitmap
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID.toByteArray(Key.CHARSET))
        val radiusData =
            ByteBuffer.allocate(Int.SIZE_BYTES * 2).putInt(borderWidth)
                .putInt(borderColor).array()
        messageDigest.update(radiusData)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is CircleBorderTransform) {
            borderColor == other.borderColor && borderWidth == other.borderWidth
        } else false
    }

    override fun hashCode(): Int {
        var hashcode = Util.hashCode(borderWidth)
        hashcode = Util.hashCode(borderColor, hashcode)
        hashcode = Util.hashCode(ID.hashCode(), hashcode)
        return hashcode
    }

}