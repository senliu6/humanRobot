package com.shciri.rosapp.ui.manualcontrol

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.graphics.Path
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shciri.rosapp.R
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.base.BaseFragment
import com.shciri.rosapp.databinding.FragmentManuaControlBinding
import com.shciri.rosapp.dmros.client.RosInit
import com.shciri.rosapp.dmros.client.RosTopic
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.dmros.tool.PointCloudEvent
import com.shciri.rosapp.dmros.tool.PublishEvent
import com.shciri.rosapp.dmros.tool.RobotPoseEvent
import com.shciri.rosapp.ui.dialog.InputDialog
import com.shciri.rosapp.ui.dialog.InputDialog.InputDialogListener
import com.shciri.rosapp.ui.view.MapView
import com.shciri.rosapp.ui.view.MyControllerView.MoveListener
import com.shciri.rosapp.utils.PointDataManager.clearMapPoints
import com.shciri.rosapp.utils.PointDataManager.getPoints
import com.shciri.rosapp.utils.SharedPreferencesUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import src.com.jilk.ros.message.custom.Pose2D
import src.com.jilk.ros.message.custom.request.SimplerResponse

/**
 * 功能：地图编辑界面
 * @author ：liudz
 * 日期：2024年12月23日
 */
class MapEditFragment : BaseFragment() {
    private lateinit var binding: FragmentManuaControlBinding
    private val executorService = RCApplication.getExecutorService()
    private var pose2D = Pose2D()
    private lateinit var pathInputDialog: InputDialog
    private val gson = Gson()
    private lateinit var pointString: java.util.ArrayList<String>
    private val pathListData: List<PathListTitle> = java.util.ArrayList();
    var type = object : TypeToken<java.util.ArrayList<PointF?>?>() {}.type
    private lateinit var nameList: ArrayList<String>

    /**
     * 操作模式 1：POI站点 2：虚拟墙
     */
    private var controlMode = 0

    //修图
    private val MODE_POI_MAP = 1

    //虚拟墙
    private val MODE_VIRTUAL_WALL = 2

    private var mVirtualWallPaths = ArrayList<Path>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManuaControlBinding.inflate(inflater, container, false)

        initView()
        return binding.root
    }

    private fun initView() {
        queryManualPath()
        binding.returnLl.setOnClickListener { view: View? -> back(view) }
        val moveListener = MoveListener { dx: Float, dy: Float ->
            RosData.cmd_vel.linear.x = dy / 2.5f
            RosData.cmd_vel.angular.z = -dx / 2f
        }
        executorService.execute {
            while (true) {
                if (RosInit.isConnect && RosTopic.cmd_velTopic != null) {
                    if (RosData.cmd_vel.linear.x != 0f || RosData.cmd_vel.angular.z != 0f) {
                        RosTopic.cmd_velTopic.publish(RosData.cmd_vel)
                        send_zero = true
                    } else {
                        if (send_zero) RosTopic.cmd_velTopic.publish(
                            RosData.cmd_vel
                        )
                        send_zero = false
                    }
                }
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        binding.controllerView.setMoveListener(moveListener)
        binding.rosMap.isSetGoal = true

        //添加充电点
        binding.addChargePoi.setOnClickListener {
            if (rosServiceCaller != null && RosInit.isConnect) {
                rosServiceCaller.callMapMgrPoiService(
                    RosData.currentMapName,
                    rosServiceCaller.FUNC_TYPE_SAVE_POI,
                    pose2D
                ) { simplerResponse: SimplerResponse ->
                    if (simplerResponse.ret) {
                        SharedPreferencesUtil.saveObject(
                            requireActivity(),
                            "charge_poi" + RosData.currentMapID,
                            pose2D
                        )
                        toast(R.string.save_success)
                    } else {
                        toast(R.string.subscribe_fail)
                    }
                    null
                }
            } else {
                toast(R.string.ros_connect_fail)
            }
        }
        //清除虚拟墙
        binding.cleanVirtualWallTv.setOnClickListener {
            if (rosServiceCaller != null && RosInit.isConnect) {
                rosServiceCaller.callMapMgrService(
                    RosData.currentMapName,
                    rosServiceCaller.FUNC_TYPE_CLEAR_ALLWALL
                ) { simplerResponse: SimplerResponse ->
                    if (simplerResponse.ret) {
                        toast(R.string.save_success)
                        clearMapPoints(requireContext(), RosData.currentMapName)
                        binding.rosMap.postInvalidate()
                    }
                    null
                }
            } else {
                toast(R.string.ros_connect_fail)
            }
        }

        //点击开始虚拟墙
        binding.startVirtualWallTv.setOnClickListener {
            binding.rosMap.startVirtualWallState(mVirtualWallPaths)
            controlMode = MODE_VIRTUAL_WALL
        }

        //点击手动添加
        binding.addPathBt.setOnClickListener {
            binding.rosMap.setShowDBPath(false)
            binding.rosMap.startPathState()
            controlMode = MODE_POI_MAP
        }


        //点击退出
        binding.btnQuit.setOnClickListener {
            when (controlMode) {
                MODE_POI_MAP -> {
                    binding.rosMap.exitAllState()
                }

                MODE_VIRTUAL_WALL -> {
                    binding.rosMap.exitVirtualWallState()
                }

                else -> {
                    binding.rosMap.reset()
                    toast(R.string.not_select_control)
                }
            }
            controlMode = 0
        }

        //点击撤销或添加点
        binding.btnBack.setOnClickListener {
            when (controlMode) {
                MODE_POI_MAP -> {
                    binding.rosMap.addPathPoint()
                }
                MODE_VIRTUAL_WALL -> {
                    binding.rosMap.addVirtualWallPathPoint()
                }
                else -> {
                    toast(R.string.not_select_control)
                }
            }
        }

        //点击保存
        binding.btnSave.setOnClickListener {
            if (controlMode == MODE_POI_MAP) {
                //poi
                    pathInputDialog = InputDialog.Builder(requireActivity())
                        .setTitle(resources.getString(R.string.please_input_pathname))
                        .setOnConfirmClick(object : InputDialogListener {
                            override fun onConfirmClicked(inputText: String, password: String) {
                                var tmpID = 0
                                if (binding.rosMap.isAddPathState) {
                                    val point: String = gson.toJson(binding.rosMap.pathPointList)
                                    tmpID = insertManualPath(inputText, point).toInt()
                                    pointString.add(point)
                                }
                                binding.rosMap.exitAllState()
                                pathInputDialog.dismiss()
                            }
                        })
                        .build()
                    pathInputDialog.show()

            } else if (controlMode == MODE_VIRTUAL_WALL) {
                //虚拟墙
                if (rosServiceCaller != null && RosInit.isConnect) {
                    binding.rosMap.saveVirtualWallPathPoints(RosData.currentMapName)
                    mVirtualWallPaths.addAll(binding.rosMap.virtualWallPaths)
                    val mapPoints =
                        getPoints(
                            requireContext(), RosData.currentMapName
                        )
                    rosServiceCaller.callMapMgrService(
                        RosData.currentMapName,
                        rosServiceCaller.FUNC_TYPE_SAVE_WALL,
                        mapPoints
                    ) { simplerResponse: SimplerResponse ->
                        if (simplerResponse.ret) {
                            toast(R.string.save_success)
                        }
                        binding.rosMap.exitVirtualWallState()
                        null
                    }
                } else {
                    toast(R.string.ros_connect_fail)
                }
            } else {
                toast(R.string.not_select_control)
            }
        }

        binding.rosMap.isShowWall = true
    }

    var mBackPressedCallback: OnBackPressedCallback? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                Navigation.findNavController(view).navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            mBackPressedCallback as OnBackPressedCallback
        )
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        setMap()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onDestroy() {
        binding.rosMap.isSetGoal = false
        mBackPressedCallback!!.remove()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PublishEvent) {
        if ("/slam_map" == event.getMessage()) {
            setMap()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PointCloudEvent) {
        val cloud2d = event.pointCloud
        if (cloud2d.points.isNotEmpty()) {
            MapView.pointsArray = cloud2d.points
            binding.rosMap.postInvalidate()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RobotPoseEvent?) {
        val location = event?.pose;
        MapView.pose = location;
        binding.rosMap.postInvalidate();
    }

    private fun setMap() {
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

    private fun insertManualPath(name: String, point: String): Long {
        val values = ContentValues()
        values.put("name", name)
        values.put("map_id", RosData.currentMapID)
        values.put("point", point)
        return RCApplication.db.insert("manual_path", null, values)
    }

    private fun queryManualPath() {
        //查询全部数据
        val cursor = RCApplication.db.query(
            "manual_path",
            null,
            "map_id=?",
            arrayOf(RosData.currentMapID.toString()),
            null,
            null,
            null
        )
        pointString = java.util.ArrayList<String>()
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id = cursor.getInt(cursor.getColumnIndex("id"))
                @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex("name"))
                @SuppressLint("Range") val point = cursor.getString(cursor.getColumnIndex("point"))
                Log.d("CeshiTAG", "point" + point + "name" + name + "id" + id)
                pointString.add(point)
            }
//            binding.rosMap.DBPathPointList = gson.fromJson(pointString[0],type)
            binding.rosMap.setShowDBPath(true)
        }
        cursor.close()
    }

    companion object {
        private var send_zero = false
    }
    private class PathListTitle(var pathName: String, var ID: Int)
}
