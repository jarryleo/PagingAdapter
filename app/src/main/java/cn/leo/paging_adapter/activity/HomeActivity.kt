package cn.leo.paging_adapter.activity

import android.os.Bundle
import androidx.activity.viewModels
import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.base.BaseActivity
import cn.leo.paging_adapter.databinding.ActivityHomeBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.holder.CheckedHolder
import cn.leo.paging_adapter.intent.CheckListIntent
import cn.leo.paging_adapter.model.HomeViewModel
import cn.leo.paging_ktx.adapter.DifferData
import cn.leo.paging_ktx.ext.buildCheckedAdapter
import cn.leo.paging_ktx.simple.SimplePager

class HomeActivity : BaseActivity() {

    private val binding: ActivityHomeBinding by binding(R.layout.activity_home)
    private val model: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.state = model.state
        binding.event = model.event

    }

    override fun onInput() {

    }

    override fun onOutput() {
        model.output(this) {
            when (it) {
                is CheckListIntent.CheckList -> initList(it.pager)
                is CheckListIntent.CheckedMode -> {}
                is CheckListIntent.Reverse -> {}
            }
        }
    }

    private fun initList(pager: SimplePager<*, DifferData>) {
        binding.adapter = binding.rvChecked.buildCheckedAdapter {
            /*adapter.setMaxChecked(5) {
                toast("最多选择${it}个")
            }*/
            addHolder(CheckedHolder()) {
                onItemChecked {
                    model.state.isCheckedAll.set(it.isAllChecked)
                }
            }
            setPager(pager)
        }
    }
}