package cn.leo.paging_adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.leo.paging_adapter.adapter.NewsHolder
import cn.leo.paging_adapter.adapter.PlaceHolder
import cn.leo.paging_adapter.adapter.TitleHolder
import cn.leo.paging_adapter.bean.NewsBean
import cn.leo.paging_adapter.databinding.ActivityMainBinding
import cn.leo.paging_adapter.ext.inflate
import cn.leo.paging_adapter.model.NewsViewModel
import cn.leo.paging_adapter.net.view_model.ViewModelCreator
import cn.leo.paging_adapter.utils.bind
import cn.leo.paging_ktx.simple.SimplePagingAdapter
import cn.leo.paging_ktx.tools.FloatDecoration
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val model by ViewModelCreator(NewsViewModel::class.java)

    private val adapter by lazy {
        SimplePagingAdapter(NewsHolder(), TitleHolder(), PlaceHolder())
    }

    private val binding: ActivityMainBinding by inflate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRv()
    }

    private fun initRv() {
        binding.rvNews.layoutManager = LinearLayoutManager(this)
        //悬浮条目
        binding.rvNews.addItemDecoration(FloatDecoration(R.layout.item_title))
        binding.rvNews.adapter = adapter
        //点击事件
        adapter.setOnItemClickListener { a, _, position ->
            val news = a.getData(position) as? NewsBean.StoriesBean
            news?.let { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url))) }
        }
        //绑定下拉刷新状态
        adapter.bind(srl_refresh)
        //绑定数据源
        adapter.setPager(model.pager)
    }

}