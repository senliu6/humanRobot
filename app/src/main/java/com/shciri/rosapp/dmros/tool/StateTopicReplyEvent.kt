package com.shciri.rosapp.dmros.tool

import com.shciri.rosapp.rosdata.PathData
import com.shciri.rosapp.rosdata.Progress
import src.com.jilk.ros.message.Pose
import src.com.jilk.ros.message.RobotLocation
import src.com.jilk.ros.message.StateMachineReply
import src.com.jilk.ros.message.StateNotificationHeartbeat
import src.com.jilk.ros.message.custom.BatteryInfo
import src.com.jilk.ros.message.custom.ClampNotifyLocation
import src.com.jilk.ros.message.custom.NavPace
import src.com.jilk.ros.message.custom.Pose2D
import src.com.jilk.ros.message.custom.request.ImageMessage
import src.com.jilk.ros.message.custom.request.CompressedImageList

/**
 * 功能：返回类型
 * @author ：liudz
 * 日期：2023年10月17日
 */
data class StateTopicReplyEvent(val stateMachineReply: StateMachineReply) {
}

data class StateNotifyHeadEvent(val state: StateNotificationHeartbeat) {}

data class RobotLoginEvent(val robotLocation: RobotLocation) {}

data class RobotPoseEvent(val pose: Pose2D)

data class NavPaceEvent(val navPace: NavPace)

data class ClampNotifyEvent(val location: ClampNotifyLocation)

data class AirEvent(val airQualityEvent: AirQualityEvent)

data class PointCloudEvent(val pointCloud: PointCloud2d)

data class RobotPose2dEvent(val pose: Pose2D)

data class BatteryEvent(val batteryInfo: BatteryInfo)

data class VideoImageEvent(val imageList: CompressedImageList)

data class VideoImageProEvent(val imageMessage: ImageMessage)

data class VideoImageIndexEvent(val imageMessage: ImageMessage)

data class VideoImageIndexProEvent(val imageMessage: ImageMessage)

data class ProgressEvent(val progress: Progress)

data class GlobalPathEvent(val path: PathData)


