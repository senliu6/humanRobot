package com.shciri.rosapp.ui.manualcontrol

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.base.BaseFragment
import com.shciri.rosapp.databinding.FragmentMapCollectionBinding
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.dmros.tool.BitmapUtils
import com.shciri.rosapp.dmros.tool.ControlMapEvent
import com.shciri.rosapp.dmros.tool.MyPGM
import com.shciri.rosapp.dmros.tool.PublishEvent
import com.shciri.rosapp.dmros.tool.RobotPoseEvent
import com.shciri.rosapp.dmros.tool.StateTopicReplyEvent
import com.shciri.rosapp.rosdata.MapCreateCmd
import com.shciri.rosapp.rosdata.request.MapCreateRequest
import com.shciri.rosapp.rosdata.response.SimplerResponse
import com.shciri.rosapp.ui.dialog.InputDialog
import com.shciri.rosapp.ui.dialog.InputDialog.InputDialogListener
import com.shciri.rosapp.ui.dialog.WaitDialog
import com.shciri.rosapp.ui.view.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import src.com.jilk.ros.message.StateMachineRequest
import src.com.jilk.ros.message.custom.Pose2D
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 功能：采集界面
 * @author ：liudz
 * 日期：2023年11月17日
 */
class CollectionFragment : BaseFragment() {

    private lateinit var binding: FragmentMapCollectionBinding
    private var stateMachineRequest: StateMachineRequest? = null

    private var waitDialog: WaitDialog? = null

    private var inputDialog: InputDialog? = null

    private var location: Pose2D? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapCollectionBinding.inflate(inflater, container, false)
        initData()
        initView()
        return binding.root
    }

    private fun initData() {
        stateMachineRequest = StateMachineRequest()

        if (RosData.rosBitmap != null) {
            binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING)
        } else {
            val map = BitmapFactory.decodeResource(resources, R.drawable.daimon_map)
            binding.rosMap.setBitmap(map, MapView.updateMapID.RUNNING)
        }
        binding.rosMap.isShowWall = false


        waitDialog = WaitDialog.Builder(requireContext())
            .setLoadingText(getString(R.string.loading))
            .setCancelText(resources.getString(R.string.cancel))
            .build()
//        val bundle = arguments
//        if (bundle != null) {
//            val state = bundle.getInt("state")
//            if (state == 1) {
//                waitDialog!!.show()
//            }
//        }
    }

    private fun initView() {

        //点击取消采集地图
        binding.tvExitMap.setOnClickListener { v ->
            waitDialog?.show()
            CoroutineScope(Dispatchers.IO).launch {
                var cmd = MapCreateCmd().apply {
                    func_type = 2
                    map_name = ""
                    map_path = ""
                }

                val mapCreateRequest = MapCreateRequest().apply {
                    this.cmd = cmd
                }

                try {
                    rosServiceCaller?.let { caller ->
                        caller.callGetMapCreateService(mapCreateRequest) { response: src.com.jilk.ros.message.custom.request.SimplerResponse ->
                            CoroutineScope(Dispatchers.Main).launch {
                                Toaster.showShort(response.ret)
                                if (response.ret) {
                                    waitDialog?.dismiss()
                                    back(view)
                                } else {
                                    waitDialog?.dismiss()
                                    toast(R.string.noNormal)
                                }
                            }
                            null
                        }
                    } ?: run {
                        CoroutineScope(Dispatchers.Main).launch {
                            waitDialog?.dismiss()
                            Toaster.showShort(getString(R.string.ros_connect_fail))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.Main).launch {
                        waitDialog?.dismiss()
                        toast(R.string.ros_connect_fail)
                    }
                }
            }
        }

        //点击保存地图
        binding.tvSaveMap.setOnClickListener { v ->
            //在API29及之后是不需要申请的，默认是允许的
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
            } else {
                //保存图片到相册
                showInputDialog()
            }
        }

    }


    private fun showInputDialog() {
        inputDialog = InputDialog.Builder(requireContext())
            .setTitle(getString(R.string.please_input_map_name))
            .setCancelText(getString(R.string.cancel))
            .setConfirmText(getString(R.string.confirm))
            .setOnCancelClick { }
            .setOnConfirmClick(object : InputDialogListener {
                override fun onConfirmClicked(inputText: String,password:String) {
                    if (TextUtils.isEmpty(inputText)) {
                        Toaster.showShort(getString(R.string.please_input_map_name))
                    } else {
                        handleConfirmClick(inputText)
                    }
                }
            })
            .build()

        inputDialog?.show()
    }



    private fun handleConfirmClick(inputText: String) {
        // 保存图片并获取MD5
        val md5 = BitmapUtils.saveImage(
            activity,
            inputText,
            RosData.dataBaseMaxMapID + 1,
            binding.rosMap.mBitmap
        )
        if (md5.isEmpty()) {
            return
        }

        // 插入数据库
        DBInsertMap(inputText, md5)
        EventBus.getDefault().post(
            ControlMapEvent(
                "addMap",
                inputText,
                RosData.dataBaseMaxMapID + 1
            )
        )

        // 创建 MapCreateCmd 对象并赋值
        val cmd = MapCreateCmd().apply {
            func_type = 1
            map_name = inputText
            map_path = "x"
        }

        // 创建 MapCreateRequest 对象并赋值
        val mapCreateRequest = MapCreateRequest().apply {
            this.cmd = cmd
        }

        try {
            // 调用服务
            rosServiceCaller?.let { caller ->
                caller.callGetMapCreateService(mapCreateRequest) { response: src.com.jilk.ros.message.custom.request.SimplerResponse ->
                    CoroutineScope(Dispatchers.Main).launch {
                        toast(R.string.save_success)
                        if (response.ret) {
                            back(view)
                        }
                    }
                    null
                }
            } ?: run {
                Toaster.showShort(getString(R.string.ros_connect_fail))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // 结束处理
            MapView.scanning = false
            inputDialog?.dismiss()
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PublishEvent) {
        if (RosData.rosBitmap != null) {
            if (MapView.scanning) {
                binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING)
            } else {
                binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING)
            }
        } else {
            val map = BitmapFactory.decodeResource(resources, R.drawable.daimon_map)
            binding.rosMap.setBitmap(map, MapView.updateMapID.RUNNING)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: StateTopicReplyEvent) {
        val stateMachineReply = event.stateMachineReply
        Toaster.showLong(stateMachineReply.map_control.toInt())
        when (stateMachineReply.map_control.toInt()) {
            0x03 -> {

            }

            0x04 -> {
                toast(R.string.save_success)
                back(this.view)
            }

            0x05,
            0x06,
            0x07,
            0x08 -> {
//                back(this.view)
            }

            0x09 -> {}
            else -> {

            }
        }
        Log.d("CeshiTAG", "onEventReply: " + event.stateMachineReply.map_control)
    }

    private fun DBInsertMap(name: String, md5: String) {
        val values = ContentValues()
        values.put("name", name)
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        values.put("time", simpleDateFormat.format(date))
        values.put("width", binding.rosMap.mBitmap.width)
        values.put("height", binding.rosMap.mBitmap.height)
        values.put("md5", md5)
        RCApplication.db.insert("map", null, values)
    }


    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RobotPoseEvent) {
        location = event.pose
        MapView.pose = location
        binding.rosMap.postInvalidate()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        PublishEvent.readyPublish = true
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        PublishEvent.readyPublish = false
        super.onStop()
    }

    override fun onDestroy() {
        binding.rosMap.isSetGoal = false
        super.onDestroy()
    }
}