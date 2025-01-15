package com.shciri.rosapp.utils

import android.content.Context
import src.com.jilk.ros.message.Point
import java.util.*

object PointDataManager {
    private const val PREF_NAME = "PointData"
    private const val KEY_MAPS = "Maps"

    fun savePoints(context: Context, mapName: String, points: List<Point>): String {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val pointsId = generatePointsId()
        val key = getKey(mapName, pointsId)
        val pointsString = points.joinToString(";") { "${it.x},${it.y},${it.z}" }
        editor.putString(key, pointsString)
        // 添加到地图列表
        val maps = sharedPreferences.getStringSet(KEY_MAPS, mutableSetOf()) ?: mutableSetOf()
        maps.add(mapName)
        editor.putStringSet(KEY_MAPS, maps)
        editor.apply()
        return pointsId
    }

    fun getPoints(context: Context, mapName: String): Map<String, List<Point>> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val mapPoints = mutableMapOf<String, List<Point>>()

        val mapPointsIds = sharedPreferences.all.keys.filter { it.startsWith("Map_$mapName-Points_") }
        for (pointsIdKey in mapPointsIds) {
            val pointsId = pointsIdKey.substringAfterLast("-")
            val pointsString = sharedPreferences.getString(pointsIdKey, null)
            val points = pointsString?.split(";")?.mapNotNull { pointString ->
                val pointArray = pointString.split(",")
                if (pointArray.size == 3) {
                    Point(pointArray[0].toDouble(), pointArray[1].toDouble(), pointArray[2].toDouble())
                } else {
                    null
                }
            } ?: emptyList()
            mapPoints[pointsId] = points
        }

        return mapPoints
    }


    fun clearPoints(context: Context, mapName: String, pointsId: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val key = getKey(mapName, pointsId)
        editor.remove(key)
        editor.apply()
    }

    fun clearMapPoints(context: Context, mapName: String) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val mapPointsIds = sharedPreferences.all.keys.filter { it.startsWith("Map_$mapName-Points_") }
        for (pointsIdKey in mapPointsIds) {
            editor.remove(pointsIdKey)
        }
        editor.apply()
    }


    private fun getKey(mapName: String, pointsId: String): String {
        return "Map_$mapName-Points_$pointsId"
    }

    private fun generatePointsId(): String {
        return UUID.randomUUID().toString()
    }
}
