package com.shciri.rosapp.server

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.shciri.rosapp.dmros.client.RosInit
import com.shciri.rosapp.dmros.data.RosData
import com.shciri.rosapp.rosdata.FoldLine
import com.shciri.rosapp.rosdata.LocationCalibrateCmd
import com.shciri.rosapp.rosdata.MapMgrCmd
import com.shciri.rosapp.rosdata.MissionNavigateCmd
import com.shciri.rosapp.rosdata.Poi
import com.shciri.rosapp.rosdata.PoiArray
import com.shciri.rosapp.rosdata.PoiMap
import com.shciri.rosapp.rosdata.WallMap
import com.shciri.rosapp.rosdata.request.LocationRequest
import com.shciri.rosapp.rosdata.request.MapCreateRequest
import com.shciri.rosapp.rosdata.request.MapGetNamesRequest
import com.shciri.rosapp.rosdata.request.MapManagerRequest
import com.shciri.rosapp.rosdata.request.MissionNavigateRequest
import com.shciri.rosapp.rosdata.request.WallRequest
import com.shciri.rosapp.rosdata.response.GetMapNamesResponse
import com.shciri.rosapp.rosdata.response.GetWallResponse
import com.shciri.rosapp.rosdata.response.MissionResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import src.com.jilk.ros.ROSClient
import src.com.jilk.ros.Service
import src.com.jilk.ros.message.Point
import src.com.jilk.ros.message.custom.Pose2D
import src.com.jilk.ros.message.custom.request.GetHandRequest
import src.com.jilk.ros.message.custom.request.SetHandControlSrv
import src.com.jilk.ros.message.custom.request.SimplerResponse
import src.com.jilk.ros.message.custom.res.GetHandControlSrv
import kotlin.coroutines.CoroutineContext


/**
 * 功能：Ros话题服务请求
 * @author ：liudz
 * 日期：2024年04月24日
 */


class RosServiceCaller(private val rosClient: ROSClient) {

    private val TAG = "RosServiceCaller"


    private lateinit var serviceHand: Service<SetHandControlSrv, SimplerResponse>
    private lateinit var serviceGetHand: Service<GetHandRequest, GetHandControlSrv>

    private lateinit var serviceMapMgr: Service<MapManagerRequest, SimplerResponse>
    private lateinit var serviceGetMapNames: Service<MapGetNamesRequest, GetMapNamesResponse>
    private lateinit var serviceCreateMapNames: Service<MapCreateRequest, SimplerResponse>
    private lateinit var serviceMissionNavigate: Service<MissionNavigateRequest, MissionResponse>
    private lateinit var serviceLocation: Service<LocationRequest, SimplerResponse>
    private lateinit var serviceWall: Service<WallRequest, GetWallResponse>

    val FUNC_TYPE_SET_MAP = 0.toShort()//设置地图
    val FUNC_TYPE_CLEAR_ALLWALL = 1.toShort()
    val FUNC_TYPE_ADD_WALL = 2.toShort()
    val FUNC_TYPE_SAVE_WALL = 3.toShort()
    val FUNC_TYPE_CLEAR_ALLPOI = 4.toShort()
    val FUNC_TYPE_ADD_POI = 5.toShort()
    val FUNC_TYPE_SAVE_POI = 6.toShort()
    val FUNC_TYPE_CLEAR_ALLPATH = 7.toShort()
    val FUNC_TYPE_ADD_TEACH_PATH = 8.toShort()
    val FUNC_TYPE_SAVE_TEACH_PATH = 9.toShort()
    val FUNC_TYPE_ADD_TOPO_PATH = 10.toShort()
    val FUNC_TYPE_SAVE_TOPO_PATH = 11.toShort()
    val FUNC_TYPE_DELETE_MAP = 12.toShort()//删除地图
    val FUNC_TYPE_MAP_RENAME = 13.toShort()//重命名地图
    val FUNC_TYPE_SAVE_AREA = 14.toShort()


    init {
        if (RosInit.isConnect) {
            initializeServices()
        } else {
            Log.e(TAG, "ROS is not connected.")
        }
    }

    private fun initializeServices() {
        serviceHand = Service(
            "/setHandParam",
            SetHandControlSrv::class.java,
            SimplerResponse::class.java,
            rosClient
        )

        serviceGetHand = Service(
            "/getHandParam",
            GetHandRequest::class.java,
            GetHandControlSrv::class.java,
            rosClient
        )

        serviceMapMgr = Service(
            "/map_mgr_srv",
            MapManagerRequest::class.java,
            SimplerResponse::class.java,
            rosClient
        )

        serviceGetMapNames = Service(
            "/get_mapnames",
            MapGetNamesRequest::class.java,
            GetMapNamesResponse::class.java,
            rosClient
        )

        serviceCreateMapNames = Service(
            "/map_create_srv",
            MapCreateRequest::class.java,
            SimplerResponse::class.java,
            rosClient
        )

        serviceMissionNavigate = Service(
            "/mission_navigate_srv",
            MissionNavigateRequest::class.java,
            MissionResponse::class.java,
            rosClient
        )

        serviceLocation = Service(
            "/location_calibrate_srv",
            LocationRequest::class.java,
            SimplerResponse::class.java,
            rosClient
        )

        serviceWall = Service(
            "/get_wallmap",
            WallRequest::class.java,
            GetWallResponse::class.java,
            rosClient
        )
    }

    //手掌操作
    fun callHandService(
        value: String,
        angleArray: IntArray,
        speedArray: IntArray,
        strengthArray: IntArray,
        mode: Byte,
        handler: (SimplerResponse) -> Unit,
        context: CoroutineContext = Dispatchers.IO // 指定协程的执行线程为 IO 线程
    ) {
        GlobalScope.launch(context) { // 在指定的协程上下文中启动一个新的协程
            try {
                // 创建 HandControl 请求并设置属性
                val request = SetHandControlSrv()
                    .apply {
                        this.t_direction = value
                        this.angle_array = angleArray
                        this.strength_array = strengthArray
                        this.speed_array = speedArray
                        this.t_mode = mode
                    }

                // 调用 HandControl 服务
                val response = serviceHand.callBlocking(request)


                // 使用协程切换到主线程，并处理响应
                withContext(Dispatchers.Main) {
                    handler(response)
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "HandControl service call interrupted", e)
            } catch (e: Exception) {
                Log.e(TAG, "Error calling HandControl service", e)
            }
        }
    }

    //获取手指实时参数
    fun callGetHandParmer(
        value: String,
        handler: (GetHandControlSrv) -> Unit, context: CoroutineContext = Dispatchers.IO
    ) {
        GlobalScope.launch(context) {
            try {
                val request = GetHandRequest().apply {
                    this.direction = value
                }
                val response = serviceGetHand.callBlocking(request)
                // 使用协程切换到主线程，并处理响应
                withContext(Dispatchers.Main) {
                    handler(response)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calling callGetHandParmer service", e)
            }
        }

    }


    //地图管理
    fun callMapMgrService(mapName: String, funType: Short, handler: (SimplerResponse) -> Unit) {
        try {
            val request = MapManagerRequest()
            val cmd = MapMgrCmd()
            cmd.func_type = funType
            cmd.map_name = mapName
            request.cmd = cmd
            val response = serviceMapMgr.callBlocking(request)
            Handler(Looper.getMainLooper()).post { handler(response) }
        } catch (e: InterruptedException) {
            Log.e(TAG, "MapManager service call interrupted", e)
        }
    }

    //重命名
    fun callMapMgrRenameService(mapName: String,reMapName: String, funType: Short, handler: (SimplerResponse) -> Unit) {
        try {
            val request = MapManagerRequest()
            val cmd = MapMgrCmd()
            cmd.func_type = funType
            cmd.map_name = mapName
            cmd.map_rename = reMapName
            request.cmd = cmd
            val response = serviceMapMgr.callBlocking(request)
            Handler(Looper.getMainLooper()).post { handler(response) }
        } catch (e: InterruptedException) {
            Log.e(TAG, "MapManager service call interrupted", e)
        }
    }



    //虚拟墙操作
    //地图管理
    fun callMapMgrService(
        mapName: String,
        funType: Short,
        wallMapData: Map<String, List<Point>>,
        handler: (SimplerResponse) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = createMapManagerRequest(mapName, funType, wallMapData)
                val response = serviceMapMgr.callBlocking(request)
                withContext(Dispatchers.Main) {
                    handler(response)
                }
            } catch (e: InterruptedException) {
                Log.e(TAG, "MapManager service call interrupted", e)
            } catch (e: Exception) {
                Log.e(TAG, "MapManager service call failed", e)
            }
        }
    }

    private fun createMapManagerRequest(
        mapName: String,
        funType: Short,
        wallMapData: Map<String, List<Point>>
    ): MapManagerRequest {
        val cmd = MapMgrCmd().apply {
            this.func_type = funType
            this.map_name = ""
            this.wall_map = createWallMap(wallMapData)
        }

        return MapManagerRequest().apply {
            this.cmd = cmd
        }
    }

    private fun createWallMap(wallMapData: Map<String, List<Point>>): WallMap {
        val foldLines = wallMapData.entries.map { (id, pointList) ->
            FoldLine().apply {
                this.points = pointList.map { point ->
                    RosData.getActualXY(point.x.toFloat(), point.y.toFloat())
                }.toTypedArray()
                this.wall_id = id
                this.wall_type = 0
            }
        }

        return WallMap().apply {
            this.foldlines = foldLines.toTypedArray()
        }
    }


    //poi充电点操作
    fun callMapMgrPoiService(
        mapName: String,
        funType: Short,
        pose: Pose2D,
        handler: (SimplerResponse) -> Unit
    ) {
        try {
            val request = MapManagerRequest().apply {
                cmd = MapMgrCmd().apply {
                    this.func_type = funType
                    this.map_name = mapName
                    this.poi_map = PoiMap().apply {
                        pois = arrayOf(Poi().apply {
                            this.poi_id = 1
                            this.pose = pose
                            this.floor_id = 1
                            this.zone_id = 1
                            this.poi_type = 31
                            this.map_name = ""
                        })
                    }
                }
            }

            // 进行同步服务调用
            val response = serviceMapMgr.callBlocking(request)

            // 在主线程中处理响应
            Handler(Looper.getMainLooper()).post { handler(response) }

        } catch (e: InterruptedException) {
            Log.e(TAG, "MapManager service call interrupted", e)
        } catch (e: Exception) {
            Log.e(TAG, "MapManager service call failed", e)
        }
    }


    //获取地图列表信息
    fun callGetMapNamesService(
        request: MapGetNamesRequest,
        handler: (GetMapNamesResponse) -> Unit
    ) {
        try {
            val response = serviceGetMapNames.callBlocking(request)
            Handler(Looper.getMainLooper()).post { handler(response) }
        } catch (e: InterruptedException) {
            Log.e(TAG, "GetMapNames service call interrupted", e)
        }
    }


    //创建地图
    fun callGetMapCreateService(
        request: MapCreateRequest,
        handler: (SimplerResponse) -> Unit
    ) {
        try {
            val response = serviceCreateMapNames.callBlocking(request)
            Handler(Looper.getMainLooper()).post { handler(response) }
        } catch (e: InterruptedException) {
            Log.e(TAG, "GetMapNames service call interrupted", e)
        }
    }



    fun callMissionNavigateService(
        num: Short, poins: Array<Point>,type: Short,
        handler: (MissionResponse) -> Unit
    ) {
        if (!::serviceMissionNavigate.isInitialized) {
            Log.e(TAG, "serviceMissionNavigate has not been initialized.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = MissionNavigateRequest()
                val cmd = MissionNavigateCmd()
                cmd.func_type = 0
                cmd.mission_type = type
                cmd.motion_type = 0
//                    getValue<Int>(
//                        RCApplication.getContext(), "path_mode", 0,
//                        Int::class.java
//                    ).toShort()
                cmd.loop_num = num

                val poiArray = PoiArray()
                val poiList = ArrayList<Poi>()

                for (point in poins) {
                    val poi = Poi()
                    val pose = Pose2D()
                    pose.x = point.x
                    pose.y = point.y
                    poi.pose = pose
                    poi.floor_id = 1
                    poi.zone_id = 1
                    poiList.add(poi)
                }

                poiArray.pois = poiList.toTypedArray()
                cmd.poi_array = poiArray

                request.cmd = cmd

                val response = serviceMissionNavigate.callBlocking(request)
                withContext(Dispatchers.Main) {
                    handler(response)
                }

            } catch (e: InterruptedException) {
                Log.e(TAG, "GetMapNames service call interrupted", e)
            }
        }
    }

    fun callMissionNavigateChargeService(
        pose2d: Pose2D,
        type: Short,
        handler: (MissionResponse) -> Unit
    ) {
        if (!::serviceMissionNavigate.isInitialized) {
            Log.e(TAG, "serviceMissionNavigate has not been initialized.")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = MissionNavigateRequest()
                val cmd = MissionNavigateCmd()
                cmd.func_type = type
                cmd.mission_type = 2
                cmd.motion_type = 0
                val poiArray = PoiArray()
                val poiList = ArrayList<Poi>()
                val poi = Poi()
                val pose = Pose2D()
                pose.x = pose2d.x
                pose.y = pose2d.y
                pose.theta = pose2d.theta
                poi.pose = pose
                poi.floor_id = 1
                poi.zone_id = 1
                poi.poi_type = 31
                poiList.add(poi)

                poiArray.pois = poiList.toTypedArray()
                cmd.poi_array = poiArray

                request.cmd = cmd

                val response = serviceMissionNavigate.callBlocking(request)
                withContext(Dispatchers.Main) {
                    handler(response)
                }

            } catch (e: InterruptedException) {
                Log.e(TAG, "GetMapNames service call interrupted", e)
            }
        }
    }



    //辅助校准
    fun callLocationService(point: Point, handler: (SimplerResponse) -> Unit) {
        try {
            val request = LocationRequest().apply {
                cmd = LocationCalibrateCmd().apply {
                    func_type = 1
                    refer_pose = Pose2D().apply {
                        val pointSec = RosData.getActualXY(point.x.toFloat(), point.y.toFloat())
                        x = pointSec.x
                        y = pointSec.y
                    }
                }
            }
            val response = serviceLocation.callBlocking(request)
            Handler(Looper.getMainLooper()).post { handler(response) }
        } catch (e: InterruptedException) {
            Log.e(TAG, "GetMapNames service call interrupted", e)
        }
    }

    fun callWallService(   request: WallRequest,
                           handler: (GetWallResponse) -> Unit){

        try {
            val response = serviceWall.callBlocking(request)
            Handler(Looper.getMainLooper()).post { handler(response) }
        } catch (e: InterruptedException) {
            Log.e(TAG, "GetMapNames service call interrupted", e)
        }
    }


}


