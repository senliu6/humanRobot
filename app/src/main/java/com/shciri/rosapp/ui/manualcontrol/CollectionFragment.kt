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
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.databinding.FragmentMapCollectionBinding
import com.shciri.rosapp.dmros.client.RosTopic
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.dmros.tool.BitmapUtils
import com.shciri.rosapp.dmros.tool.ControlMapEvent
import com.shciri.rosapp.dmros.tool.MyPGM
import com.shciri.rosapp.dmros.tool.PublishEvent
import com.shciri.rosapp.dmros.tool.RobotPoseEvent
import com.shciri.rosapp.dmros.tool.StateTopicReplyEvent
import com.shciri.rosapp.ui.dialog.InputDialog
import com.shciri.rosapp.ui.dialog.InputDialog.InputDialogListener
import com.shciri.rosapp.ui.dialog.WaitDialog
import com.shciri.rosapp.ui.myview.MapView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import src.com.jilk.ros.message.Pose
import src.com.jilk.ros.message.StateMachineRequest
import src.com.jilk.ros.message.requestparam.RequestMapControlParameter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 功能：采集界面
 * @author ：liudz
 * 日期：2023年11月17日
 */
class CollectionFragment : Fragment() {

    private lateinit var binding: FragmentMapCollectionBinding
    private var stateMachineRequest: StateMachineRequest? = null

    private var waitDialog: WaitDialog? = null

    private var inputDialog: InputDialog? = null
    private val executorService = RCApplication.getExecutorService()

    private var localReceiver: LocalReceiver? = null

    var localBroadcastManager: LocalBroadcastManager? = null

    //    private PhotoView photoView;
    private var bitmap: Bitmap? = null

    private var location: Pose? = null

    private val handler = Handler()


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

        val intentFilter = IntentFilter()
        intentFilter.addAction(RosData.MAP)
        intentFilter.addAction(RosData.TF)
        intentFilter.addAction(RosData.TOAST)
        localReceiver = LocalReceiver()
        localBroadcastManager = LocalBroadcastManager.getInstance(requireContext())
        localBroadcastManager!!.registerReceiver(localReceiver!!, intentFilter)

        waitDialog = WaitDialog.Builder(requireContext())
            .setLoadingText(getString(R.string.loading))
            .setCancelText(resources.getString(R.string.cancel))
            .build()
        val bundle = arguments
        if (bundle != null) {
            val state = bundle.getInt("state")
            if (state == 1) {
                waitDialog!!.show()
            }
        }
    }

    private fun initView() {

        //点击取消采集地图
        //点击取消采集地图
        binding.tvExitMap.setOnClickListener { v ->
            stateMachineRequest!!.map_control = 5
            RosTopic.publishStateMachineRequest(stateMachineRequest)
            waitDialog!!.show()
        }
        //点击保存地图
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

    inner class LocalReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                RosData.TF -> plotRoute(RosData.BaseLink.x, RosData.BaseLink.y)
                RosData.WATCH -> plotMap()
                RosData.TOAST -> {
                    val hint = intent.getStringExtra("Hint")
                    Toaster.showShort(hint)
                }

                else -> {}
            }
        }
    }

    fun plotRoute(x: Int, y: Int) {
        val tX = RosData.MapData.poseX + x
        val tY = RosData.MapData.poseY + y
        binding.rosMap.setRobotPosition(
            tX.toFloat(),
            (RosData.map.info.height - tY).toFloat(), RosData.BaseLink.yaw * 100 - 120, true
        )
    }

    fun plotMap() {
        Log.d("CeshiTAG", "look x=" + RosData.MapData.poseX + "  y=" + RosData.MapData.poseY)
        val pgm = MyPGM()
        val pix: IntArray = pgm.readData(
            RosData.map.info.width,
            RosData.map.info.height,
            5,
            RosData.map.data,
            RosData.MapData.poseX,
            RosData.MapData.poseY
        ) //P5-Gray image
        bitmap = Bitmap.createBitmap(
            RosData.map.info.width,
            RosData.map.info.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap?.setPixels(
            pix,
            0,
            RosData.map.info.width,
            0,
            0,
            RosData.map.info.width,
            RosData.map.info.height
        )
        val invert = Matrix()
        invert.setScale(1f, -1f) //镜像翻转以与真实地图对应
        val rosBitmap =
            Bitmap.createBitmap(
                bitmap!!,
                0,
                0,
                bitmap?.width ?: 0,
                bitmap?.height ?: 0,
                invert,
                true
            )
        binding.rosMap.setBitmap(rosBitmap, MapView.updateMapID.RUNNING)
    }

    private fun showInputDialog() {
        inputDialog = InputDialog.Builder(requireContext())
            .setTitle(getString(R.string.please_input_map_name))
            .setCancelText(getString(R.string.cancel))
            .setConfirmText(getString(R.string.confirm))
            .setOnCancelClick { v: View? ->
                stateMachineRequest!!.map_control = 5
                RosTopic.publishStateMachineRequest(stateMachineRequest)
                waitDialog!!.show()
            }
            .setOnConfirmClick(object : InputDialogListener {
                override fun onConfirmClicked(inputText: String) {
                    if (TextUtils.isEmpty(inputText)) {
                        Toaster.showShort(getString(R.string.please_input_map_name))
                    } else {
                        val MD5 = BitmapUtils.saveImage(
                            inputText,
                            RosData.dataBaseMaxMapID + 1,
                            binding.rosMap.mBitmap
                        )
                        if (MD5 == "") {
                            return
                        }
                        val requestMapControlParameter =
                            RequestMapControlParameter()
                        requestMapControlParameter.map_id = inputText
                        RosTopic.publishControlParameterTopic(requestMapControlParameter)
                        DBInsertMap(inputText, MD5)
                        EventBus.getDefault()
                            .post(
                                ControlMapEvent(
                                    "addMap",
                                    inputText,
                                    RosData.dataBaseMaxMapID + 1
                                )
                            )
                        stateMachineRequest!!.map_control = 4
                        RosTopic.publishStateMachineRequest(stateMachineRequest)
                        MapView.scanning = false
                        inputDialog!!.dismiss()
                    }
                }
            })
            .build()
        inputDialog!!.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PublishEvent) {
        if ("/watch/carto_map" == event.getMessage()) {
            if (RosData.rosBitmap != null) {
                if (MapView.scanning) {
                    binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.SCANNING)
                } else {
                    binding.rosMap.setBitmap(RosData.rosBitmap, MapView.updateMapID.RUNNING)
                }
            } else {
                val map = BitmapFactory.decodeResource(resources, R.drawable.daimon_map)
                binding.rosMap.setBitmap(map, MapView.updateMapID.RUNNING)
            }
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
                Toaster.showShort(getString(R.string.save_success))
            }

            0x05 -> {}
            0x06 -> {}
            0x07 -> {}
            0x08 -> {}
            0x09 -> {}
            else -> {

            }
        }
        waitDialog!!.dismiss()
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
        this.localBroadcastManager?.unregisterReceiver(localReceiver!!)
        super.onDestroy()
    }
}