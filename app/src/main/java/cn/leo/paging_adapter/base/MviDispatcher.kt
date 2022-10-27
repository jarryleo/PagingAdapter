package cn.leo.paging_adapter.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * @author : Leo
 * @date : 2022/10/25
 * @description : mvi 分发
 */
open class MviDispatcher<E> : ViewModel(), DefaultLifecycleObserver {

    companion object {
        private const val DEFAULT_QUEUE_LENGTH = 10
        private const val START_VERSION = -1
    }

    private var version = START_VERSION
    private var currentVersion = START_VERSION
    private var observerCount = 0

    data class ConsumeOnceValue<E>(
        var consumeCount: Int = 0,
        val value: E
    )

    private val _sharedFlow: MutableSharedFlow<ConsumeOnceValue<E>>? by lazy {
        MutableSharedFlow(
            onBufferOverflow = BufferOverflow.DROP_LATEST,
            extraBufferCapacity = initQueueMaxLength(),
            replay = initQueueMaxLength()
        )
    }

    protected open fun initQueueMaxLength(): Int {
        return DEFAULT_QUEUE_LENGTH
    }

    /**
     * 输出
     */
    fun output(lifecycleOwner: LifecycleOwner?, observer: (E) -> Unit) {
        if (lifecycleOwner == null) return
        currentVersion = version
        observerCount++
        var lo = lifecycleOwner
        if (lifecycleOwner is Fragment) {
            lo = lifecycleOwner.viewLifecycleOwner
        }
        lo.lifecycle.addObserver(this)
        lo.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _sharedFlow?.collect {
                    if (version <= currentVersion) return@collect
                    if (it.consumeCount >= observerCount) return@collect
                    it.consumeCount++
                    observer.invoke(it.value)
                }
            }
        }
    }

    /**
     * 发送事件，model内部发送
     */
    protected suspend fun sendResult(event: E) {
        version++
        _sharedFlow?.emit(ConsumeOnceValue(value = event))
    }

    /**
     * 输入,由界面输入事件
     */
    fun input(event: E) {
        viewModelScope.launch { onHandle(event) }
    }

    /**
     * model 处理界面输入事件
     */
    protected open suspend fun onHandle(event: E) {}


    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        observerCount--
    }
}