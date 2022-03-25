package cn.leo.paging_adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.leo.paging_adapter.databinding.ActivityMainBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.holder.NewsHolder
import cn.leo.paging_adapter.holder.PlaceHolder
import cn.leo.paging_adapter.holder.TitleHolder
import cn.leo.paging_adapter.model.NewsViewModel
import cn.leo.paging_ktx.ext.buildAdapter

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val model: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.adapter = binding.rvNews.buildAdapter {
            addHolder(NewsHolder()) {
                onItemClick {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.data.url)))
                }
            }
            addHolder(TitleHolder(), true)
            addHolder(PlaceHolder())
            setPager(model.pager)
        }
    }
}