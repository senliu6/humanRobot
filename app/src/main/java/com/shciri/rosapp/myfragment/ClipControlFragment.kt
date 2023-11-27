package com.shciri.rosapp.myfragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.databinding.FragmentClipControlBinding
import com.shciri.rosapp.dmros.client.RosTopic
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.ui.myview.MyControllerView.INVISIBLE
import com.shciri.rosapp.ui.myview.MyControllerView.MoveListener
import src.com.jilk.ros.message.requestparam.ClampControl

/**
 * 功能：抱夹调试界面
 * @author ：liudz
 * 日期：2023年11月16日
 */
class ClipControlFragment : Fragment() {
    private lateinit var binding: FragmentClipControlBinding
    private var type: Byte = 0
    private var controlStatus: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClipControlBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        val moveListener =
            MoveListener { dx, dy ->
//             Log.d("ManuaControlFragment", "move " + dx + ", " + dy);
                RosData.cmd_vel.linear.x = dy / 2.5f
                RosData.cmd_vel.angular.z = -dx / 2f
            }
        binding.controllerView.setMoveListener(moveListener)

        //点击发送
        binding.tvConfirm.setOnClickListener {
            if (type.toInt() == 0) {
                Toaster.showShort("请选择抱或者取")
            } else {
                if (!TextUtils.isEmpty(binding.editX.text) && !TextUtils.isEmpty(binding.editY.text) && !TextUtils.isEmpty(
                        binding.editH.text
                    )
                    && !TextUtils.isEmpty(binding.editBottomH.text)
                ) {
                    val clampControl = ClampControl()
                    clampControl.goal_id = binding.editX.text.toString().toByte()
                    clampControl.plan_mode = binding.editY.text.toString().toByte()
                    clampControl.top_height = binding.editH.text.toString().toShort()
                    clampControl.bottom_height = binding.editBottomH.text.toString().toShort()
                    clampControl.claw = type
                    RosTopic.publishClampControl(clampControl)
                } else {
                    Toaster.showShort("请输入正确的值")
                }
            }
        }
        //点击选择
        binding.radioList.setOnCheckedChangeListener { _: RadioGroup?, i: Int ->
            if (i == R.id.btnClown) {
                binding.btnClown.isChecked = true
                type = 0x01
            } else if (i == R.id.btnPut) {
                binding.btnPut.isChecked = true
                type = 0x02
            }
        }
        //点击取消
        binding.tvCancel.setOnClickListener {
            val clampControl = ClampControl()
            clampControl.exit_task = true
            RosTopic.publishClampControl(clampControl)
        }
        //点击急停
        binding.btnStop.setOnClickListener {
            val clampControl = ClampControl()
            clampControl.emergency_stop = true
            RosTopic.publishClampControl(clampControl)
        }
        //点击复位
        binding.btnReset.setOnClickListener {
            val clampControl = ClampControl()
            clampControl.reset = true
            RosTopic.publishClampControl(clampControl)
        }
        //点击返回
        binding.returnLl.setOnClickListener { Navigation.findNavController(it).navigateUp() }

        //点击RB键
        binding.btnRB.setOnClickListener {
            RosData.cmd_vel.linear.x = 0f
        }
        //点击遥控开关
        binding.btnCloseControl.setOnClickListener {
            if (controlStatus) {
                binding.controllerView.visibility = View.GONE
                controlStatus = false
                binding.btnCloseControl.text = "遥控开"
            } else {
                binding.controllerView.visibility = View.VISIBLE
                controlStatus = true
                binding.btnCloseControl.text = "遥控关"
            }
        }

    }
}
