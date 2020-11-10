package cn.leo.paging_adapter.net.view_model

import cn.leo.paging_adapter.net.utils.KResult
import cn.leo.paging_adapter.net.utils.withIO
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll

/**
 * @author : leo
 * @date : 2020/6/24
 */
suspend inline fun <T> Deferred<T>.result() =
    withIO {
        try {
            val data = this@result.await()
            KResult.success(data)
        } catch (e: Exception) {
            KResult.failure<T>(e)
        }
    }

suspend inline fun <T> Collection<Deferred<T>>.result() =
    withIO {
        try {
            val data = this@result.awaitAll()
            KResult.success(data)
        } catch (e: Exception) {
            KResult.failure<List<T>>(e)
        }
    }
