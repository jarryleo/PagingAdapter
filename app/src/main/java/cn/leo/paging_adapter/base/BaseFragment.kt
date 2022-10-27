package cn.leo.paging_adapter.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

/**
 * @author : ling luo
 * @date : 2022/10/27
 * @description : Fragment 基类
 */
abstract class BaseFragment : Fragment() {
    protected open fun onInitData() {}
    protected open fun onOutput() {}
    protected open fun onInput() {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnBackPressed()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitData()
        onOutput()
        onInput()
    }

    private fun addOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!onBackPressed()) {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }

    protected open fun onBackPressed(): Boolean {
        return true
    }
}