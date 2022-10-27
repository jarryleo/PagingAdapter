package cn.leo.paging_adapter.activity

import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.base.BaseActivity
import cn.leo.paging_adapter.databinding.ActivityMainBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.holder.NewsHolder
import cn.leo.paging_adapter.holder.PlaceHolder
import cn.leo.paging_adapter.holder.TitleHolder
import cn.leo.paging_adapter.intent.NewsIntent
import cn.leo.paging_adapter.model.NewsViewModel
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.ext.buildAdapter
import cn.leo.paging_ktx.simple.SimplePager

class MainActivity : BaseActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val model: NewsViewModel by viewModels()

    override fun onOutput() {
        model.output(this) {
            when (it) {
                is NewsIntent.NewsStates -> initNews(it.pager)
            }
        }
    }

    override fun onInput() {
        model.input(NewsIntent.TEST)
    }

    private fun initNews(pager: SimplePager<*, DifferData>) {
        binding.adapter = binding.rvNews.buildAdapter {
            addHolder(NewsHolder()) {
                onItemClick {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.data.url)))
                }
            }
            addHolder(TitleHolder(), isFloatItem = true)
            addHolder(PlaceHolder())
            setPager(pager)
        }
    }
}