package com.shciri.rosapp.myfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.shciri.rosapp.databinding.FragmentMapSetBinding

/**
 * 功能：地图设置功能
 * @author ：liudz
 * 日期：2023年10月30日
 */
class MapSetFragment : Fragment() {
    private lateinit var binding: FragmentMapSetBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapSetBinding.inflate(layoutInflater)
        initView()
        return binding.root

    }

    private fun initView() {
        binding.btnDynamic.setOnClickListener { setStaticMode(true) }
        binding.btnStatic.setOnClickListener { setStaticMode(false) }
        binding.returnLl.setOnClickListener { view ->
            Navigation.findNavController(view).navigateUp()
        }
    }

    fun setStaticMode(staticMode: Boolean) {
        binding.btnDynamic.isSelected = staticMode
        binding.btnStatic.isSelected = !staticMode
    }
}