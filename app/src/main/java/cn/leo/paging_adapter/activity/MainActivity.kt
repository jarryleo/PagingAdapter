package cn.leo.paging_adapter.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.leo.paging_adapter.R
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

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val model: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.output(this) {
            when (it) {
                is NewsIntent.NewsStates -> initAdapter(it.pager)
            }
        }
    }

    private fun initAdapter(pager: SimplePager<*, DifferData>) {
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