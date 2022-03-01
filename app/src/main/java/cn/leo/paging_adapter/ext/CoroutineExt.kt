package cn.leo.paging_adapter.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.*

/**
 * 协程拓展
 * @author : ling luo
 * @date : 2019-06-10
 */


/**
 * io协程，运行在io线程，生命周期不安全
 */
fun io(block: suspend CoroutineScope.() -> Unit): Job {
    val supervisorJob = SupervisorJob()
    return CoroutineScope(Dispatchers.IO + supervisorJob).launch {
        block()
    }
}

/**
 * 可以绑定生命周期的io协程
 */
fun LifecycleOwner.io(block: suspend CoroutineScope.() -> Unit): Job {
    val supervisorJob = SupervisorJob()
    CoroutineScope(Dispatchers.IO + supervisorJob).launch {
        block()
    }
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            supervisorJob.cancel()
            lifecycle.removeObserver(this)
        }
    })
    return supervisorJob
}

/**
 * 运行在主线程的协程，生命周期不安全
 */
fun main(block: suspend CoroutineScope.() -> Unit): Job {
    val supervisorJob = SupervisorJob()
    return CoroutineScope(Dispatchers.Main + supervisorJob).launch {
        block()
    }
}

/**
 * 可以绑定生命周期的main协程
 */
fun LifecycleOwner.main(block: suspend CoroutineScope.() -> Unit): Job {
    val supervisorJob = SupervisorJob()
    CoroutineScope(Dispatchers.Main + supervisorJob).launch {
        block()
    }
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            supervisorJob.cancel()
            lifecycle.removeObserver(this)
        }
    })
    return supervisorJob
}

/**
 * 运行在io线程的有返回值的协程
 */
fun <T> wait(block: suspend CoroutineScope.() -> T): Deferred<T> {
    val supervisorJob = SupervisorJob()
    return CoroutineScope(Dispatchers.IO + supervisorJob).async {
        block()
    }
}

/**
 * 合并多个协程，并获取结果列表
 */
suspend fun <T> merge(vararg deferred: Deferred<T>): List<T> {
    return deferred.toMutableList().awaitAll()
}

/**
 * 协程内转换到IO协程
 */
suspend fun <T> withIO(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO, block)

/**
 * 协程内转换到主线程
 */
suspend fun <T> withMain(block: suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Main, block)



