package cn.leo.paging_adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_adapter.databinding.ActivityMainBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.holder.NewsHolder
import cn.leo.paging_adapter.holder.PlaceHolder
import cn.leo.paging_adapter.holder.TitleHolder
import cn.leo.paging_adapter.model.NewsViewModel
import cn.leo.paging_ktx.simple.SimplePagingAdapter
import cn.leo.paging_ktx.tools.FloatDecoration

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val model: NewsViewModel by viewModels()
    private val adapter = SimplePagingAdapter(NewsHolder(), TitleHolder(), PlaceHolder())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.adapter = adapter
        binding.model = model
        initRv()
    }

    private fun initRv() {
        //悬浮条目
        binding.rvNews.addItemDecoration(FloatDecoration(R.layout.item_title))
        //点击事件
        adapter.setOnItemClickListener { a, _, position ->
            val news = a.getData(position) as? NewsBean.StoriesBean
            news?.let { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url))) }
        }
    }

}