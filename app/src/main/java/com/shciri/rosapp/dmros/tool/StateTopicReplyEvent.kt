package com.shciri.rosapp.dmros.tool

import src.com.jilk.ros.message.StateMachineReply
import src.com.jilk.ros.message.StateNotificationHeartbeat

/**
 * 功能：返回类型
 * @author ：liudz
 * 日期：2023年10月17日
 */
data class StateTopicReplyEvent(val stateMachineReply: StateMachineReply) {
}

data class StateNotifyHeadEvent(val state: StateNotificationHeartbeat) {}
