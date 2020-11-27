package cn.leo.paging_ktx

/**
 * @author : ling luo
 * @date : 2020/11/27
 * @description : 字符串数据
 */
data class StringData(
    val string: CharSequence
) : DifferData {
    override fun areItemsTheSame(d: DifferData): Boolean {
        return (d as? StringData)?.string == string
    }

    override fun toString(): String {
        return string.toString()
    }
}