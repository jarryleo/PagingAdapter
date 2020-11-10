package cn.leo.paging_adapter.net.http

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * @author : leo
 * @date : 2020/4/28
 * Retrofit 生产 service 类
 */

fun <T> Class<T>.create(config: ServiceCreator.Config.() -> Unit = {}): T {
    return ServiceCreator.create(this, config)
}

@Suppress("UNCHECKED_CAST")
object ServiceCreator {
    private val retrofitMap = ConcurrentHashMap<String, Retrofit>()
    private val apiMap = ConcurrentHashMap<String, Any>()

    fun <T> create(service: Class<T>, config: Config.() -> Unit = {}): T {
        val cfg = Config()
        config(cfg)
        val apiKey = service.name + cfg.baseUrl
        var api = apiMap[apiKey] as? T
        if (api != null) {
            return api
        }
        val retrofit = retrofitMap[cfg.baseUrl] ?: Retrofit.Builder().apply {
            baseUrl(cfg.baseUrl)
            client(cfg.httpClient)
            callAdapterFactories().addAll(cfg.callAdapterFactoryList)
            converterFactories().addAll(cfg.converterFactoryList)
        }.build()
        retrofitMap[cfg.baseUrl] = retrofit
        api = retrofit.create(service)
        apiMap[apiKey] = api as Any
        return api
    }

    class Config {
        var baseUrl: String = ""
            set(value) {
                field = if (value.endsWith("/")) value else "$value/"
            }
        var httpClient = OkHttp3Creator.build()
        val converterFactoryList =
            mutableListOf(GsonConverterFactory.create())
        val callAdapterFactoryList =
            mutableListOf(CoroutineCallAdapterFactory())
    }
}