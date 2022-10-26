package cn.leo.paging_adapter.repository

import cn.leo.paging_adapter.App
import cn.leo.paging_adapter.net.Apis
import cn.leo.paging_adapter.net.Urls
import cn.leo.paging_adapter.net.http.OkHttp3Creator
import cn.leo.paging_adapter.net.http.ServiceCreator
import cn.leo.paging_adapter.net.interceptor.CacheInterceptor
import cn.leo.paging_adapter.net.interceptor.LoggerInterceptor

/**
 * @author : leo
 * @date : 2020/4/29
 */
open class BaseRepository {

    companion object {
        val api by lazy {
            ServiceCreator.create(Apis::class.java) {
                baseUrl = Urls.baseUrlZhiHu
                httpClient = OkHttp3Creator.build {
                    //缓存文件夹
                    cacheDir = App.context?.cacheDir
                    //网络请求日志打印拦截器
                    addInterceptor(LoggerInterceptor())
                    //接口缓存拦截器
                    addInterceptor(CacheInterceptor())
                }
            }
        }
    }
}