package cn.leo.paging_adapter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.leo.paging_adapter.databinding.ActivityMainBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.ext.toast
import cn.leo.paging_adapter.holder.NewsHolder
import cn.leo.paging_adapter.holder.PlaceHolder
import cn.leo.paging_adapter.holder.TitleHolder
import cn.leo.paging_adapter.model.NewsViewModel
import cn.leo.paging_ktx.ext.buildCheckedAdapter
import cn.leo.paging_ktx.ext.isChecked

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by binding(R.layout.activity_main)
    private val model: NewsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.adapter = binding.rvNews.buildCheckedAdapter {
            addHolder(NewsHolder()) {
                onItemClick {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.data.url)))
                }
                onItemChildClick(R.id.iv_cover) {
                    toast(it.data.title)
                }
            }
            addHolder(TitleHolder(), isClickChecked = false) {
                onChecked {
                    toast("${it.position} = ${it.isChecked} ${it.checkedCount}/${it.allCanCheckedCount}")
                }
                onItemLongClick {
                    it.isChecked = !it.isChecked
                }
            }
            addHolder(PlaceHolder())
            setPager(model.pager)
        }
    }
}