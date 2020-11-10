package cn.leo.paging_adapter.net.http

import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * @author : leo
 * @date : 2020/4/28
 * OkHttp3 生成配置
 */
object OkHttp3Creator {
    fun build(
        okHttp3Build: OkHttpClient.Builder.() -> Unit = {},
        config: Config.() -> Unit = {}
    ): OkHttpClient {
        val cfg = Config()
        config(cfg)
        val builder = OkHttpClient.Builder()
        cfg.apply {
            builder
                .connectTimeout(connectTimeoutSecond, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSecond, TimeUnit.SECONDS)
                .writeTimeout(writeTimeoutSecond, TimeUnit.SECONDS)
            cacheDir?.let { dir ->
                builder.cache(Cache(dir, cacheSize))
            }
            interceptors.forEach {
                builder.addInterceptor(it)
            }
            if (sslSocketFactory == null) {
                val trustAllCerts = arrayOf<TrustManager>(trustManager)
                try {
                    val sslContext = SSLContext.getInstance("SSL")
                    sslContext.init(null, trustAllCerts, SecureRandom())
                    sslSocketFactory = sslContext.socketFactory
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (hostnameVerifier == null) {
                hostnameVerifier = HostnameVerifier { _, _ -> true }
            }
            builder.sslSocketFactory(sslSocketFactory!!, trustManager)
            builder.hostnameVerifier(hostnameVerifier!!)
        }
        okHttp3Build(builder)
        return builder.build()
    }

    class Config {
        /**
         * 连接超时时间
         * 单位：秒
         */
        var connectTimeoutSecond = 30L
            set(value) {
                field = if (value > 0) value else 30L
            }

        /**
         * 读取超时时间
         * 单位：秒
         */
        var readTimeoutSecond = 30L
            set(value) {
                field = if (value > 0) value else 30L
            }

        /**
         * 写入超时时间
         * 单位：秒
         */
        var writeTimeoutSecond = 30L
            set(value) {
                field = if (value > 0) value else 30L
            }

        /**
         * 缓存目录
         */
        var cacheDir: File? = null

        /**
         * 缓存大小
         */
        var cacheSize: Long = 30 * 1024 * 1024

        /**
         * 支持https操作
         */
        var trustManager = object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
        var sslSocketFactory: SSLSocketFactory? = null
        var hostnameVerifier: HostnameVerifier? = null

        /**
         * 拦截器列表
         */
        internal val interceptors = mutableListOf<Interceptor>()

        /**
         * 添加拦截器
         */
        fun addInterceptor(interceptor: Interceptor) {
            interceptors.add(interceptor)
        }
    }
}