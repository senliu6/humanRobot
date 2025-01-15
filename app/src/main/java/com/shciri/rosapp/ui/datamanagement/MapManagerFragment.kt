package com.shciri.rosapp.ui.datamanagement

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.reflect.TypeToken
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.base.BaseFragment
import com.shciri.rosapp.databinding.FragmentMapManagerBinding
import com.shciri.rosapp.dmros.client.RosInit
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.rosdata.MapCreateCmd
import com.shciri.rosapp.rosdata.request.MapCreateRequest
import com.shciri.rosapp.rosdata.request.MapGetNamesRequest
import com.shciri.rosapp.rosdata.response.GetMapNamesResponse
import com.shciri.rosapp.ui.datamanagement.adapter.ActionType
import com.shciri.rosapp.ui.datamanagement.adapter.MapItem
import com.shciri.rosapp.ui.datamanagement.adapter.MapItemAdapter
import com.shciri.rosapp.ui.dialog.InputDialog
import com.shciri.rosapp.ui.dialog.WaitDialog
import com.shciri.rosapp.ui.view.MapView
import com.shciri.rosapp.utils.SharedPreferencesUtil
import com.shciri.rosapp.utils.ToolsUtil
import com.shciri.rosapp.utils.occupancyGridToBitmap
import src.com.jilk.ros.message.custom.request.SimplerResponse

/**
 * 功能：
 * @author ：liudz
 * 日期：2024年12月19日
 */
class MapManagerFragment : BaseFragment() {
    private lateinit var binding: FragmentMapManagerBinding
    private lateinit var mapAdapter: MapItemAdapter
    private var waitDialog: WaitDialog? = null
    private lateinit var renameDialog: InputDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapManagerBinding.inflate(inflater, container, false)
        initView()
        initData()
        return binding.root
    }

    private fun initView() {
        setupRecyclerView()
        waitDialog = WaitDialog.Builder(requireContext())
            .setLoadingText(getString(R.string.loading))
            .setCancelText(resources.getString(R.string.cancel))
            .build()
        // 返回
        binding.returnLl.setOnClickListener {
            Navigation.findNavController(requireView()).navigateUp()
        }
        //采集
        binding.btnCollection.setOnClickListener {
            waitDialog!!.show()

            if (!RosInit.offLineMode) {
                executorService.submit {
                    val cmd = MapCreateCmd()
                    cmd.func_type = 0
                    cmd.map_name = ""
                    cmd.map_path = ""
                    val mapCreate = MapCreateRequest()
                    mapCreate.cmd = cmd
                    if (rosServiceCaller != null && RosInit.isConnect) {
                        // 创建 Handler 用于在 15 秒后提示失败
                        val handler = Handler(Looper.getMainLooper())
                        val timeoutRunnable = Runnable {
                            waitDialog!!.dismiss()
                            toast(R.string.create_map_fail) // 提示超时
                        }
                        // 延迟 10 秒执行超时提示
                        handler.postDelayed(timeoutRunnable, 15000)

                        // 调用 ROS 服务
                        rosServiceCaller.callGetMapCreateService(mapCreate) { response: SimplerResponse ->
                            try {
                                // 如果在超时前收到响应，移除超时提示
                                handler.removeCallbacks(timeoutRunnable)
                                Toaster.showShort(response.ret)
                                if (response.ret) {
                                    waitDialog!!.dismiss()
                                    MapView.scanning = true
                                    val bundle = Bundle()
                                    bundle.putInt("state", 1)
                                    Navigation.findNavController(it)
                                        .navigate(R.id.collectionFragment, bundle)
                                } else {
                                    waitDialog!!.dismiss()
                                    toast(R.string.create_map_fail)
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                            null
                        }
                    } else {
                        toast(R.string.ros_connect_fail)
                        waitDialog!!.dismiss()
                    }
                }
            } else {
                toast(R.string.no_contral_offline)
                waitDialog!!.dismiss()
            }
        }
        //更新地图列表
        binding.btnUpdate.setOnClickListener {
            fetchRemoteMapData()
        }
    }

    private fun initData() {
        // 从本地缓存获取已保存的地图信息
        val cachedMapList = SharedPreferencesUtil.getTypeValue(
            requireContext(),
            "map_list",
            emptyList<MapItem>(),
            object : TypeToken<List<MapItem>>() {}.type
        ).map {
            MapItem(
                name = it.name,
                isCurrent = it.isCurrent,
                thumbnailUrl = it.thumbnailUrl?.let { base64Str ->
                    ToolsUtil.base64ToBitmap(
                        base64Str
                    )?.let { ToolsUtil.bitmapToBase64(it) }
                }
                // 使用 base64ToBitmap 转换回 Bitmap, 如果需要再转换为 Base64
            )
        }

        // 只有在获取到缓存数据时才更新 UI
        if (cachedMapList.isNotEmpty()) {
            mapAdapter.updateData(cachedMapList)
        }

        fetchRemoteMapData()
    }

    private fun setupRecyclerView() {
        mapAdapter = MapItemAdapter(mutableListOf()) { mapItem, actionType ->
            handleItemAction(mapItem, actionType)
        }
        val gridLayoutManager = GridLayoutManager(context, 3)
        binding.recyclerMapList.apply {
            layoutManager = gridLayoutManager
            adapter = mapAdapter
        }
    }

    private fun fetchRemoteMapData() {
        executorService.submit {
            val getMapNamesRequest = MapGetNamesRequest()
            if (rosServiceCaller != null && RosInit.isConnect) {
                rosServiceCaller.callGetMapNamesService(getMapNamesRequest) { response: GetMapNamesResponse ->
                    try {
                        val remoteMapList =
                            response.map_name_list.map_names.mapIndexed { index, name ->
                                val occupancyGrid = response.map_name_list.preview_map[index]
                                val thumbnailBitmap = occupancyGridToBitmap(occupancyGrid)
                                MapItem(
                                    name = name,
                                    isCurrent = name == response.map_name_list.default_map_name, // 假设默认不是当前地图
                                    thumbnailUrl = thumbnailBitmap?.let {
                                        ToolsUtil.bitmapToBase64(
                                            it
                                        )
                                    } // 将 Bitmap 转换为 Base64 字符串
                                )
                            }

                        // 保存到本地缓存：转换 Bitmap 为 Base64 字符串
                        val base64List = remoteMapList.map {
                            MapItem(
                                name = it.name,
                                isCurrent = it.isCurrent,
                                thumbnailUrl = it.thumbnailUrl
                            )
                        }
                        SharedPreferencesUtil.saveTypeValue(
                            requireContext(),
                            "map_list",
                            base64List
                        )

                        // 更新 RecyclerView 数据
                        activity?.runOnUiThread {
                            mapAdapter.updateData(remoteMapList) // 刷新数据
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                toast(R.string.ros_connect_fail)
            }
        }
    }

    private fun handleItemAction(mapItem: MapItem, actionType: ActionType) {
        when (actionType) {
            ActionType.SWITCH -> {
                // 切换地图
                if (rosServiceCaller != null && RosInit.isConnect) {
                    rosServiceCaller.callMapMgrService(
                        mapItem.name,
                        rosServiceCaller.FUNC_TYPE_SET_MAP
                    ) { simplerResponse: SimplerResponse ->
                        if (simplerResponse.ret) {
                            toast(R.string.save_success)
                            RosData.currentMapID = mapItem.name
                            RosData.currentMapName = mapItem.name
                            fetchRemoteMapData()
                        }
                        null
                    }
                }
            }

            ActionType.DELETE -> {
                // 删除地图
                if (rosServiceCaller != null && RosInit.isConnect) {
                    if (mapItem.name != RosData.currentMapName) {
                        rosServiceCaller.callMapMgrService(
                            mapItem.name,
                            rosServiceCaller.FUNC_TYPE_DELETE_MAP
                        ) { simplerResponse: SimplerResponse ->
                            if (simplerResponse.ret) {
                                toast(R.string.save_success)
                                fetchRemoteMapData()
                            }
                            null
                        }
                    } else {
                        toast(R.string.current_map_cannot_delete)
                    }

                } else {
                    toast(R.string.ros_connect_fail)
                }
            }

            ActionType.EDIT -> {
                // 重命名地图逻辑
                renameDialog = InputDialog.Builder(requireActivity())
                    .setTitle(resources.getString(R.string.please_input_new_map))
                    .setOnConfirmClick(object : InputDialog.InputDialogListener {
                        override fun onConfirmClicked(inputText: String, password: String) {
                            if (rosServiceCaller != null && RosInit.isConnect) {
                                if (inputText.isNotEmpty()) {
                                    rosServiceCaller.callMapMgrRenameService(
                                        mapItem.name,
                                        inputText,
                                        rosServiceCaller.FUNC_TYPE_MAP_RENAME
                                    ) { simplerResponse: SimplerResponse ->
                                        if (simplerResponse.ret) {
                                            toast(R.string.save_success)
                                            fetchRemoteMapData()
                                            renameDialog.dismiss()
                                        } else {
                                            renameDialog.dismiss()
                                        }
                                        null
                                    }
                                } else {
                                    toast(R.string.please_input_map_name)
                                }

                            } else {
                                toast(R.string.ros_connect_fail)
                            }
                        }
                    })
                    .build()
                renameDialog.show()
            }

        }
    }
}
