package com.shciri.rosapp.myfragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.hjq.shape.view.ShapeButton
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.databinding.FragmentClipTestBinding
import com.shciri.rosapp.dmros.client.RosTopic
import com.shciri.rosapp.dmros.tool.ClampNotifyEvent
import com.shciri.rosapp.dmros.tool.PublishEvent
import com.shciri.rosapp.dmros.tool.RobotPoseEvent
import com.shciri.rosapp.ui.myview.MapView
import com.shciri.rosapp.utils.ToolsUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import src.com.jilk.ros.message.custom.ClampLocation
import src.com.jilk.ros.message.custom.ClampNotifyLocation
import src.com.jilk.ros.message.requestparam.ClampHardwareControl
import src.com.jilk.ros.message.requestparam.EnterManual

/**
 * 功能：抱夹测试界面
 * @author ：liudz
 * 日期：2023年10月26日
 */

class ClipFragment : androidx.fragment.app.Fragment() {
    // 使用 ViewBinding
    private lateinit var binding: FragmentClipTestBinding
    private lateinit var clampHardwareControl: ClampHardwareControl
    private lateinit var enterManual: EnterManual

    private val handler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null

    private var location = ClampLocation()
    private var notification = ClampNotifyLocation()


    // 使用伴生对象创建 Fragment 实例
    companion object {
        fun newInstance(): ClipFragment {
            return ClipFragment()
        }
    }

    enum class ButtonType(val value: Int) {
        RISE(1),
        DECLINE(2),
        CLIP(3),
        LOOSEN(4),
        RESET(5),
        AGO(6),
        AFTER(7)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 使用 ViewBinding 初始化视图
        binding = FragmentClipTestBinding.inflate(inflater, container, false)
        initView()
        initData()
        return binding.root
    }

    private fun initData() {
        clampHardwareControl = ClampHardwareControl()
        enterManual = EnterManual()
        enterManual.enter_manual = true
        RosTopic.publishEnterManual(enterManual)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        //点击上升
        setButtonTouchListener(binding.btnRise, ButtonType.RISE)
        //点击下降
        setButtonTouchListener(binding.btnDecline, ButtonType.DECLINE)
        //点击夹抱
        setButtonTouchListener(binding.btnClip, ButtonType.CLIP)
        //点击松开
        setButtonTouchListener(binding.btnLoosen, ButtonType.LOOSEN)
        //点击货叉归零
        binding.btnReset.setOnClickListener { setDefault(ButtonType.RESET.value) }
        //点击货叉向前
        setButtonTouchListener(binding.btnAgo, ButtonType.AGO)
        //点击货叉向后
        setButtonTouchListener(binding.btnAfter, ButtonType.AFTER)
        //点击返回
        binding.returnLl.setOnClickListener { Navigation.findNavController(it).navigateUp() }

        binding.checkMotor.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {

            } else {

            }
        }

        binding.tvStart.setOnClickListener { publishClampLocation(1) }
        binding.tvStop.setOnClickListener { publishClampLocation(2) }


    }

    //恢复默认值且发送
    private fun setDefault(type: Int) {
        clampHardwareControl.clip = false
        clampHardwareControl.decline = false
        clampHardwareControl.fork_after = false
        clampHardwareControl.fork_ago = false
        clampHardwareControl.fork_reset = false
        clampHardwareControl.rise = false
        clampHardwareControl.loosen = false
        when (type) {
            1 -> clampHardwareControl.rise = true
            2 -> clampHardwareControl.decline = true
            3 -> clampHardwareControl.clip = true
            4 -> clampHardwareControl.loosen = true
            5 -> clampHardwareControl.fork_reset = true
            6 -> clampHardwareControl.fork_ago = true
            7 -> clampHardwareControl.fork_after = true
        }
        RosTopic.publishClampHardWareControl(clampHardwareControl)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonTouchListener(button: ShapeButton, type: ButtonType) {
        button.setOnTouchListener { v, event ->
            handleButtonTouchEvent(event, type, v)
        }
    }

    private fun handleButtonTouchEvent(event: MotionEvent, type: ButtonType, view: View): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startLongPress(type, view)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopLongPress(view)
            }
        }
        return true
    }

    private fun startLongPress(type: ButtonType, view: View) {
        longPressRunnable = object : Runnable {
            @SuppressLint("ResourceAsColor")
            override fun run() {
                setDefault(type.value)
                view.setBackgroundColor(view.resources.getColor(R.color.button_select_start))
                handler.postDelayed(this, 100)
            }
        }
        handler.postDelayed(longPressRunnable!!, 0)
    }


    private fun stopLongPress(view: View) {
        longPressRunnable?.let {
            handler.removeCallbacks(it)
        }
        setDefault(0)
        view.setBackgroundColor(view.resources.getColor(R.color.blue_1f4e96))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        enterManual.enter_manual = false
        RosTopic.publishEnterManual(enterManual)
        longPressRunnable = null
    }

    private fun publishClampLocation(type: Int) {
        val input = binding.editInput.text.toString().toDouble()
        //先归零，0.1是底层协议沟通后的结果
        location.lifting_num = 0.1
        location.motor_num = 0.1
        location.left_motor_num = 0.1
        location.right_motor_num = 0.1

        if (binding.checkMotor.isChecked) {
            location.lifting_num = input
        }

        if (binding.checkClamp.isChecked) {
            location.motor_num = input
        }

        if (binding.checkLClamp.isChecked) {
            location.left_motor_num = input
        }

        if (binding.checkRClamp.isChecked) {
            location.right_motor_num = input
        }
        when (type) {
            1 -> {
                location.start = true
                location.stop = false
            }

            2 -> {
                location.start = false
                location.stop = true
            }
        }
        RosTopic.publishClampLocation(location)
    }

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ClampNotifyEvent) {
        notification = event.location
        binding.checkMotor.text = "升降：${String.format("%.2f", notification.lifting_num)}"
        binding.checkClamp.text = "夹抱：${String.format("%.2f", notification.motor_num)}"
        binding.checkLClamp.text = "左夹抱：${String.format("%.2f", notification.left_motor_num)}"
        binding.checkRClamp.text = "右夹抱：${String.format("%.2f", notification.right_motor_num)}"
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}