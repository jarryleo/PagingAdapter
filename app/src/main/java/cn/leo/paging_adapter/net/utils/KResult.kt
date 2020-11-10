package cn.leo.paging_adapter.net.utils

import java.io.Serializable

@Suppress("UNUSED", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")
class KResult<out T> constructor(
    val value: Any?
) : Serializable {

    val isSuccess: Boolean get() = value !is Failure

    val isFailure: Boolean get() = value is Failure

    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }

    companion object {

        fun <T> success(value: T): KResult<T> =
            KResult(value)

        fun <T> failure(exception: Throwable): KResult<T> =
            KResult(
                createFailure(
                    exception
                )
            )
    }

    internal class Failure(
        @JvmField
        val exception: Throwable
    ) : Serializable {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}

internal fun createFailure(exception: Throwable): Any =
    KResult.Failure(exception)

fun KResult<*>.throwOnFailure() {
    if (value is KResult.Failure) throw value.exception
}

inline fun <R> runCatching(block: () -> R): KResult<R> {
    return try {
        KResult.success(block())
    } catch (e: Throwable) {
        KResult.failure(e)
    }
}

inline fun <T, R> T.runCatching(block: T.() -> R): KResult<R> {
    return try {
        KResult.success(block())
    } catch (e: Throwable) {
        KResult.failure(e)
    }
}
@Suppress("UNCHECKED_CAST")
fun <T> KResult<T>.getOrThrow(): T {
    throwOnFailure()
    return value as T
}

@Suppress("UNCHECKED_CAST")
inline fun <R, T : R> KResult<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}
@Suppress("UNCHECKED_CAST")
fun <R, T : R> KResult<T>.getOrDefault(defaultValue: R): R {
    if (isFailure) return defaultValue
    return value as T
}

@Suppress("UNCHECKED_CAST")
inline fun <R, T> KResult<T>.fold(
    onSuccess: (value: T) -> R,
    onFailure: (exception: Throwable) -> R
): R {

    return when (val exception = exceptionOrNull()) {
        null -> onSuccess(value as T)
        else -> onFailure(exception)
    }
}
@Suppress("UNCHECKED_CAST")
inline fun <R, T> KResult<T>.map(transform: (value: T) -> R): KResult<R> {
    return when {
        isSuccess -> KResult.success(
            transform(value as T)
        )
        else -> KResult(value)
    }
}
@Suppress("UNCHECKED_CAST")
inline fun <R, T> KResult<T>.mapCatching(transform: (value: T) -> R): KResult<R> {
    return when {
        isSuccess -> runCatching { transform(value as T) }
        else -> KResult(value)
    }
}


inline fun <R, T : R> KResult<T>.recover(transform: (exception: Throwable) -> R): KResult<R> {
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> KResult.success(
            transform(exception)
        )
    }
}

inline fun <R, T : R> KResult<T>.recoverCatching(transform: (exception: Throwable) -> R): KResult<R> {
    val value = value // workaround for inline classes BE bug
    return when (val exception = exceptionOrNull()) {
        null -> this
        else -> runCatching { transform(exception) }
    }
}

inline fun <T> KResult<T>.onFailure(action: (exception: Throwable) -> Unit): KResult<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}
@Suppress("UNCHECKED_CAST")
inline fun <T> KResult<T>.onSuccess(action: (value: T) -> Unit): KResult<T> {
    if (isSuccess) action(value as T)
    return this
}
