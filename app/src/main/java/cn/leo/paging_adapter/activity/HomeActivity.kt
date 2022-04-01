package cn.leo.paging_adapter.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.leo.paging_adapter.R
import cn.leo.paging_adapter.bean.TitleBean
import cn.leo.paging_adapter.databinding.ActivityHomeBinding
import cn.leo.paging_adapter.ext.binding
import cn.leo.paging_adapter.ext.singleClick
import cn.leo.paging_adapter.holder.CheckedHolder
import cn.leo.paging_ktx.ext.buildCheckedAdapter

class HomeActivity : AppCompatActivity() {

    private val binding: ActivityHomeBinding by binding(R.layout.activity_home)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val list = (0..10).map { TitleBean("测试$it") }
        val adapter = binding.rvChecked.buildCheckedAdapter {
            addHolder(CheckedHolder()) {
                onChecked {
                    Log.e("onChecked = ${it.position}", "isAllChecked= ${it.isAllChecked}")
                }
            }
        }
        binding.adapter = adapter
        adapter.setList(lifecycleScope, list)

        binding.btnJump.singleClick {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.btnAll.singleClick {
            adapter.checkedAll()
        }
        binding.btnReverse.singleClick {
            adapter.reverseChecked()
        }

    }
}