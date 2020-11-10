package cn.leo.paging_adapter.net.utils

import java.lang.reflect.ParameterizedType

/**
 * 通过反射,获得定义Class时声明的父类的范型参数的类型.
 * 如public BookManager extends GenericManager<Book>
 *
 * this is The class to introspect
 * @return the first generic declaration,
 * or <code>Object.class</code> if cannot be determine
 */
fun <T> Class<*>.getSuperClassGenericType(): Class<T> {
    return getSuperClassGenericType(0)
}

/**
 * 通过反射,获得定义Class时声明的父类的范型参数的类型.
 * 如public BookManager extends GenericManager<Book>
 *
 * this is clazz The class to introspect
 * @param index the Index of the generic declaration,start from 0.</Book>
 */
@Suppress("UNCHECKED_CAST")
fun <T> Class<*>.getSuperClassGenericType(index: Int): Class<T> {
    var cls: Class<*>? = this
    var genType = cls?.genericSuperclass
    while (genType !is ParameterizedType) {
        cls = cls?.superclass
        requireNotNull(cls)
        genType = cls.genericSuperclass
    }
    val params = genType.actualTypeArguments
    require(!(index >= params.size || index < 0))
    return if (params[index] !is Class<*>) {
        throw IllegalArgumentException()
    } else params[index] as Class<T>
}