package cn.leo.paging_adapter.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * @author : ling luo
 * @date : 2022/10/27
 * @description : Activity 基类
 */
abstract class BaseActivity: AppCompatActivity() {
    protected open fun onInitData() {}
    protected open fun onOutput() {}
    protected open fun onInput() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onInitData()
        onOutput()
        onInput()
    }
}