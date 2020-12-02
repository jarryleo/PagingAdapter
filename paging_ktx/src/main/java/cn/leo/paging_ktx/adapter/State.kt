package cn.leo.paging_ktx.adapter

/**
 * @author : leo
 * @date : 2020/11/10
 * @description : Adapter 刷新或加载更多状态
 */
sealed class State {
    object Loading : State()
    class Success(val noMoreData: Boolean) : State()
    object Error : State()
}