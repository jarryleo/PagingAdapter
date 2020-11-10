package cn.leo.paging_adapter.image.glide

import android.content.Context
import cn.leo.paging_adapter.net.http.OkHttp3Creator
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Created by Leo on 2017/8/7.
 */
@GlideModule
class OkHttpAppGlideModule : AppGlideModule() {
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        val client: OkHttpClient = OkHttp3Creator.build()
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(client)
        )
    }

    override fun applyOptions(
        context: Context,
        builder: GlideBuilder
    ) {
        builder.setMemoryCache(LruResourceCache(30 * 1024 * 1024))
        builder.setBitmapPool(LruBitmapPool(20 * 1024 * 1024))
    }
}