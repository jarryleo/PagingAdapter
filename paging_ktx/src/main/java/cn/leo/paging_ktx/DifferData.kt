package cn.leo.paging_ktx

/**
 * @author : ling luo
 * @date : 2020/11/10
 * @description : 数据比较类
 */
interface DifferData {

    fun areItemsTheSame(d: DifferData): Boolean {
        return this == d
    }

    fun areContentsTheSame(d: DifferData): Boolean {
        return this == d
    }

    fun getChangePayload(d: DifferData): Any? {
        return null
    }

}