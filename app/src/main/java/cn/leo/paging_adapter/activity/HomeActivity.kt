package cn.leo.paging_adapter.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.databinding.ActivityHomeBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.ext.toast
import cn.leo.paging_adapter.holder.CheckedHolder
import cn.leo.paging_adapter.model.HomeViewModel
import cn.leo.paging_ktx.ext.buildCheckedAdapter

class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by binding(R.layout.activity_home)
    private val model: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.state = model.state
        binding.event = model.event
        binding.adapter = binding.rvChecked.buildCheckedAdapter {
            adapter.setMaxChecked(5) {
                toast("最多选择${it}个")
            }
            addHolder(CheckedHolder()) {
                onItemChecked {
                    model.state.isCheckedAll.set(it.isAllChecked)
                }
            }
            setPager(model.pager)
        }
    }
}