package cn.leo.paging_adapter.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.databinding.ActivityHomeBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.ext.toast
import cn.leo.paging_adapter.holder.CheckedHolder
import cn.leo.paging_adapter.holder.TitleHolder
import cn.leo.paging_adapter.model.HomeViewModel1
import cn.leo.paging_ktx.ext.buildCheckedAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by binding(R.layout.activity_home)
    private val model: HomeViewModel1 by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.init()
        binding.state = model.state
        binding.event = model.event
        binding.adapter = binding.rvChecked.buildCheckedAdapter {
            adapter.setMaxChecked(5) {
                toast("最多选择${it}个")
            }
            addHolder(TitleHolder(), isFloatItem = true)
            addHolder(CheckedHolder()) {
                onItemChecked {
                    model.state.isCheckedAll.set(it.isAllChecked)
                }
            }
            lifecycleScope.launch {
                model.data.collect {
                    setData(model.viewModelScope, it)
                }
            }
        }
    }
}