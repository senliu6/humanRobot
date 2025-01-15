package com.shciri.rosapp.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.base.BaseFragment
import com.shciri.rosapp.databinding.FragmentChooseTaskBinding
import com.shciri.rosapp.dmros.client.RosInit
import com.shciri.rosapp.dmros.data.LanguageType
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.dmros.data.Settings
import com.shciri.rosapp.dmros.tool.GlobalPathEvent
import com.shciri.rosapp.dmros.tool.PointCloudEvent
import com.shciri.rosapp.dmros.tool.ProgressEvent
import com.shciri.rosapp.dmros.tool.PublishEvent
import com.shciri.rosapp.dmros.tool.RobotPoseEvent
import com.shciri.rosapp.rosdata.PathData
import com.shciri.rosapp.rosdata.Progress
import com.shciri.rosapp.rosdata.request.MapGetNamesRequest
import com.shciri.rosapp.rosdata.request.WallRequest
import com.shciri.rosapp.rosdata.response.GetMapNamesResponse
import com.shciri.rosapp.rosdata.response.MissionResponse
import com.shciri.rosapp.ui.view.MapView
import com.shciri.rosapp.utils.LanguageUtil
import com.shciri.rosapp.utils.PointDataManager
import com.shciri.rosapp.utils.SharedPreferencesUtil.Companion.getObject
import com.shciri.rosapp.utils.SharedPreferencesUtil.Companion.getValue
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import src.com.jilk.ros.message.Point
import src.com.jilk.ros.message.custom.Pose2D

/**
 * 功能：首页
 * @author ：liudz
 * 日期：2024年12月18日
 */


class ChooseTaskFragment : BaseFragment() {
    private lateinit var binding: FragmentChooseTaskBinding
    private var location: Pose2D? = null
    private lateinit var pointString: java.util.ArrayList<String>
    private val gson = Gson()
    var type = object : TypeToken<ArrayList<PointF?>?>() {}.type
    private var isFirstTime = true
    private var pathData: PathData? = null


    private lateinit var nameList: ArrayList<String>
    private var currentPointList: Array<Point> = arrayOf()
    private var pointNavList: ArrayList<Point> = ArrayList()

    var pathPointList = java.util.ArrayList<PointF>()

    private var MISSION_TYPE_NORMAL_POI: Short = 0;
    private var MISSION_TYPE_MULTI_POI: Short = 1;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseTaskBinding.inflate(inflater, container, false)
        initView()
        initData()
        return binding.root
    }

    @SuppressLint("DefaultLocale")
    private fun initData() {
        setMapImage()
//        fetchRemoteMapData()
        queryManualPath(isFirstTime)


    }


    private fun initView() {
        binding.shapeHand.setOnClickListener {
            Navigation.findNavController(
                requireView()
            ).navigate(R.id.action_nav_home_to_handControlFragment)
        }

        binding.shapeSystem.setOnClickListener {
            Navigation.findNavController(
                requireView()
            ).navigate(R.id.action_nav_home_to_systemSetFragment)
        }
        binding.ivExpand.setOnClickListener { (requireActivity() as TaskControlActivity).openDrawerLayout() }

        binding.shapeLocation.setOnClickListener {
            Navigation.findNavController(
                requireView()
            ).navigate(R.id.action_nav_home_to_mapEditFragment)
        }

        binding.shapeMapManager.setOnClickListener {
            Navigation.findNavController(
                requireView()
            ).navigate(R.id.action_nav_home_to_mapManagerFragment)
        }

        // 设置选项选择监听器
        binding.spinnerModeOne.setOnItemSelectedListener { _, position: Int, _, _ ->
            // 根据 position 执行不同操作
            pathPointList = gson.fromJson(pointString[position], type)
            currentPointList =
                pathPointList.map { point ->
                    RosData.getActualXY(
                        point.x,
                        point.y
                    )
                }
                    .toTypedArray()
            binding.rosMap.DBPathPointList = gson.fromJson(pointString[position], type)

        }

        binding.btnStart.setOnClickListener {

            if (rosServiceCaller != null && RosInit.isConnect) {
                if (currentPointList.isNotEmpty() && binding.editNumber.text.toString()
                        .toInt() > 0
                ) {
                    rosServiceCaller.callMissionNavigateService(
                        binding.editNumber.text.toString().toShort(),
                        currentPointList, MISSION_TYPE_MULTI_POI,
                    ) {
                        run {
                            if (it.ret) {
                                Toaster.showShort("Success")
                            } else {
                                Toaster.showShort("Fail")
                            }
                        }
                    }
                } else {
                    Toaster.showLong(getString(R.string.please_check_task))
                }
            } else {
                toast(R.string.subscribe_fail)
            }

        }
        binding.btnEnd.setOnClickListener {

            if (rosServiceCaller != null && RosInit.isConnect) {
                val savedPose = Pose2D()
                rosServiceCaller.callMissionNavigateChargeService(
                    savedPose,
                    1.toShort()
                ) { simplerResponse: MissionResponse ->
                    if (simplerResponse.ret) {
                        toast(R.string.mission_sucess)
                    } else {
                        toast(R.string.mission_fail)
                    }
                    null
                }
            } else {
                toast(R.string.subscribe_fail)
            }
        }

        binding.btnExitCharge.setOnClickListener {
            if (rosServiceCaller != null && RosInit.isConnect) {
                val savedPose = Pose2D()
                rosServiceCaller.callMissionNavigateChargeService(
                    savedPose,
                    1.toShort()
                ) { simplerResponse: MissionResponse ->
                    if (simplerResponse.ret) {
                        toast(R.string.mission_sucess)
                    } else {
                        toast(R.string.mission_fail)
                    }
                    RosData.isTimeTask = false
                    null
                }
            } else {
                toast(R.string.ros_connect_fail)
            }
        }

        binding.btnCharge.setOnClickListener {
            if (rosServiceCaller != null && RosInit.isConnect) {
                val savedPose = getObject(
                    requireActivity(), "charge_poi" + RosData.currentMapID,
                    Pose2D::class.java
                )
                if (savedPose != null) {
                    rosServiceCaller.callMissionNavigateChargeService(
                        savedPose,
                        0.toShort()
                    ) { simplerResponse: MissionResponse ->
                        if (simplerResponse.ret) {
                            toast(R.string.mission_sucess)
                        } else {
                            toast(R.string.mission_fail)
                        }
                        null
                    }
                } else {
                    toast(R.string.add_charge_poi)
                }
            } else {
                toast(R.string.ros_connect_fail)
            }
        }

        binding.rosMap.upLocation = true

        binding.btnPointNav.setOnClickListener {
            pointNavList.add(binding.rosMap.onClickPointPost)
            if (rosServiceCaller != null && RosInit.isConnect) {
                if (pointNavList[0].x.toInt() == 0 && pointNavList[0].y.toInt() == 0) {
                    toast(R.string.please_click_map)
                } else {
                    rosServiceCaller.callMissionNavigateService(
                        1,
                        pointNavList.toTypedArray(), MISSION_TYPE_NORMAL_POI,
                    ) {
                        run {
                            if (it.ret) {
                                Toaster.showShort("Success")
                            } else {
                                Toaster.showShort("Fail")
                            }
                        }
                    }
                }
            } else {
                toast(R.string.ros_connect_fail)
            }

        }

        val language = getValue(
            RCApplication.getContext(), Settings.LANGUAGE, LanguageType.CHINESE.getLanguage(),
            String::class.java
        )

        if (language == LanguageType.ENGLISH.getLanguage()) {
            val layoutParams = binding.btnPointNav.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topToBottom = binding.btnEnd.id
            binding.btnPointNav.layoutParams = layoutParams
        }


    }

    override fun onResume() {
        EventBus.getDefault().register(this)
        super.onResume()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PublishEvent) {
        if ("/slam_map" == event.getMessage()) {
            setMapImage()
            fetchRemoteMapData()

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

    @SuppressLint("SetTextI18n")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RobotPoseEvent) {
        location = event.pose
        MapView.pose = location
        binding.rosMap.postInvalidate()
        val locationText = String.format("(%.1f,%.1f)", location!!.x, location!!.y)
        binding.tvLocation.text =
            String.format(resources.getString(R.string.now_location), locationText)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: ProgressEvent) {
        val progress: Progress = event.progress
        binding.arcStatus.setCurrentState(progress.work_mode.toInt())
        binding.tvTaskStatus.text =
            String.format(resources.getString(R.string.task_status), progress.work_mode)
        binding.circularProgressBattery.setProgress(progress.mission_progress.toInt())

        val linearX = RosData.cmd_vel.linear.x
        // 使用字符串模板和 format 函数直接格式化，保留一位小数
        val result = "%.1f m/s".format(linearX)
        binding.tvSpeed.text = String.format(resources.getString(R.string.now_speed), result)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: GlobalPathEvent) {
        pathData = event.path
        binding.rosMap.isShowRobotPath = true
        binding.rosMap.setLocalPath(ArrayList(pathData!!.poses.map { pose ->
            PointF(
                pose.pose.position.x.toFloat(),
                pose.pose.position.y.toFloat()
            )
        }))

    }

    private fun setMapImage() {
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

    private fun fetchRemoteMapData() {
        executorService.submit {
            val getMapNamesRequest = MapGetNamesRequest()
            if (rosServiceCaller != null && RosInit.isConnect) {
                rosServiceCaller.callGetMapNamesService(getMapNamesRequest) { response: GetMapNamesResponse ->
                    try {
                        RosData.currentMapName = response.map_name_list.default_map_name
                        RosData.currentMapID = response.map_name_list.default_map_name
                        queryManualPath(isFirstTime)
                        isFirstTime = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val wallRequest = WallRequest()
                rosServiceCaller.callWallService(wallRequest) {
                    if (isAdded) {
                        PointDataManager.clearMapPoints(requireContext(), RosData.currentMapName)
                        for (foldLine in it.wall_map.foldlines) {
                            val mapName =
                                RosData.currentMapName; // 假设 RosData.currentMapName 是存储当前地图名称的地方
                            context?.let { it1 ->
                                PointDataManager.savePoints(it1, mapName,
                                    foldLine.points.map { point ->
                                        RosData.getPixelXY(
                                            point.x,
                                            point.y
                                        )
                                    })
                            }
                        }
                        binding.rosMap.reset()
                    }
                }

            } else {
                toast(R.string.ros_connect_fail)
            }

        }
    }

    private fun queryManualPath(update: Boolean) {
        //查询全部数据
        val cursor = RCApplication.db.query(
            "manual_path",
            null,
            "map_id=?",
            arrayOf(RosData.currentMapID),
            null,
            null,
            null
        )
        pointString = ArrayList()
        nameList = ArrayList()
        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") val id = cursor.getInt(cursor.getColumnIndex("id"))
                @SuppressLint("Range") val name = cursor.getString(cursor.getColumnIndex("name"))
                @SuppressLint("Range") val point = cursor.getString(cursor.getColumnIndex("point"))
                Log.d("CeshiTAG", "point" + point + "name" + name + "id" + id)
                pointString.add(point)
                nameList.add(name)
            }
            binding.spinnerModeOne.setItems(nameList)
            binding.rosMap.setShowDBPath(true)
            if (update) {
                binding.rosMap.DBPathPointList = gson.fromJson(pointString[0], type)
                if (nameList.isNotEmpty()) {
                    binding.spinnerModeOne.selectedIndex = 0;
                }
            }
        }
        cursor.close()
    }


    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }
}