package cn.leo.paging_adapter.utils

import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.view.StatusPager
import cn.leo.paging_ktx.adapter.State
import cn.leo.paging_ktx.simple.SimplePagingAdapter
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState

/**
 * @author : leo
 * @date : 2020/12/3
 * @description : 适配器业务拓展
 */

/**
 * 绑定下拉刷新状态
 */
fun SimplePagingAdapter.bind(smartRefreshLayout: SmartRefreshLayout) {
    //状态页
    var statePager = smartRefreshLayout.getTag(1766613352) as? StatusPager
    if (statePager == null) {
        statePager = StatusPager.builder(smartRefreshLayout)
            .emptyViewLayout(R.layout.state_empty)
            .loadingViewLayout(R.layout.state_loading)
            .errorViewLayout(R.layout.state_error)
            .addRetryButtonId(R.id.btn_retry)
            .setRetryClickListener { _, _ ->
                this.refresh()
            }
            .build()
        smartRefreshLayout.setTag(1766613352, statePager)
    }
    //设置下拉刷新
    smartRefreshLayout.setOnRefreshListener {
        this.refresh()
    }
    //上拉加载更多
    smartRefreshLayout.setOnLoadMoreListener {
        this.retry()
    }
    //下拉刷新状态
    this.addOnRefreshStateListener {
        when (it) {
            is State.Loading -> {
                //如果是手动下拉刷新，则不展示loading页
                if (smartRefreshLayout.state != RefreshState.Refreshing) {
                    statePager.showLoading()
                }
                smartRefreshLayout.resetNoMoreData()
            }
            is State.Success -> {
                statePager.showContent()
                smartRefreshLayout.finishRefresh(true)
                smartRefreshLayout.setNoMoreData(it.noMoreData)
            }
            is State.Error -> {
                statePager.showError()
                smartRefreshLayout.finishRefresh(false)
            }
        }
    }
    //加载更多状态
    this.addOnLoadMoreStateListener {
        when (it) {
            is State.Loading -> {
                //重置上拉加载状态，显示加载loading
                smartRefreshLayout.resetNoMoreData()
            }
            is State.Success -> {
                if (it.noMoreData) {
                    smartRefreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    smartRefreshLayout.finishLoadMore(true)
                }
            }
            is State.Error -> {
                smartRefreshLayout.finishLoadMore(false)
            }
        }
    }
}