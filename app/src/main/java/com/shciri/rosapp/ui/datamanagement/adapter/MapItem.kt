package com.shciri.rosapp.ui.datamanagement.adapter

import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shciri.rosapp.R
import com.shciri.rosapp.databinding.ItemMapBinding
import com.shciri.rosapp.utils.ToolsUtil

// 地图数据模型
data class MapItem(
    val name: String,              // 地图名称
    val isCurrent: Boolean,        // 是否为当前地图
    val thumbnailUrl: String?       // 缩略图的 URL
)

enum class ActionType {
    SWITCH, DELETE,EDIT
}

class MapItemAdapter(private val mapItems: MutableList<MapItem>, private val onItemAction: (MapItem, ActionType) -> Unit) :
    RecyclerView.Adapter<MapItemAdapter.MapViewHolder>() {

    inner class MapViewHolder(private val binding: ItemMapBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.M)
        fun bind(mapItem: MapItem) {
            with(binding) {
                tvMapName.text = mapItem.name

                btnAction.isEnabled = !mapItem.isCurrent

                if (mapItem.isCurrent) {
                    shapeMapManager.shapeDrawableBuilder.strokeColor = shapeMapManager.context.resources.getColor(
                        R.color.red
                    )
                    shapeMapManager.shapeDrawableBuilder.strokeSize = 8;
                    shapeMapManager.shapeDrawableBuilder.buildBackgroundDrawable()
                    btnAction.shapeDrawableBuilder.solidColor = btnAction.context.resources.getColor(R.color.red)
                    btnAction.text = shapeMapManager.context.getString(R.string.now_map)
                    btnAction.shapeDrawableBuilder.intoBackground()
                } else {
                    shapeMapManager.shapeDrawableBuilder.strokeColor = shapeMapManager.context.resources.getColor(
                        R.color.gray
                    )
                    shapeMapManager.shapeDrawableBuilder.strokeSize = 5;
                    shapeMapManager.shapeDrawableBuilder.buildBackgroundDrawable()
                    btnAction.shapeDrawableBuilder.solidColor = btnAction.context.resources.getColor(R.color.blue_bg)
                    btnAction.text = shapeMapManager.context.getString(R.string.switchs)
                    btnAction.shapeDrawableBuilder.intoBackground()

                }

                // 如果 thumbnailUrl 是 Base64 字符串，则将其转换为 Bitmap
                val bitmap = mapItem.thumbnailUrl?.let { ToolsUtil.base64ToBitmap(it) }

                // 如果 Bitmap 非空且未回收，则加载到 ImageView
                if (bitmap != null && !bitmap.isRecycled) {
                    val options = RequestOptions()
                        .disallowHardwareConfig() // 禁用硬件配置
                    Glide.with(imgThumbnail.context)
                        .load(bitmap)
                        .apply(options)
                        .into(imgThumbnail)
                }

                btnAction.setOnClickListener {
                    onItemAction(mapItem, ActionType.SWITCH)
                }

                btnDelete.setOnClickListener {
                    onItemAction(mapItem, ActionType.DELETE)
                }

                btnEdit.setOnClickListener {
                    onItemAction(mapItem, ActionType.EDIT)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapViewHolder {
        val binding = ItemMapBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MapViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MapViewHolder, position: Int) {
        holder.bind(mapItems[position])
    }

    override fun getItemCount(): Int = mapItems.size

    // Method to update data and notify adapter
    fun updateData(newData: List<MapItem>) {
        mapItems.clear()
        mapItems.addAll(newData)
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}


