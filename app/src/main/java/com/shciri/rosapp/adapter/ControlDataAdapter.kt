package com.shciri.rosapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hjq.shape.view.ShapeButton
import com.shciri.rosapp.R
import com.shciri.rosapp.rosdata.ControlDataS

class ControlDataAdapter(
    private var controlDataList: MutableList<ControlDataS>,
    private val onExecuteClick: (ControlDataS) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ControlDataAdapter.ControlDataViewHolder>() {

    // ViewHolder类，管理每个item的视图
    inner class ControlDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_view)
        val executeButton: ShapeButton = itemView.findViewById(R.id.btn_execute)
        val deleteButton: ShapeButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ControlDataViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_control_data, parent, false)
        return ControlDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: ControlDataViewHolder, position: Int) {
        val controlData = controlDataList[holder.adapterPosition]
        holder.textView.text = controlData.name

        // 执行按钮点击事件
        holder.executeButton.setOnClickListener {
            onExecuteClick(controlDataList[holder.adapterPosition])
        }

        // 删除按钮点击事件
        holder.deleteButton.setOnClickListener {
            onDeleteClick(holder.adapterPosition)
        }
    }


    override fun getItemCount(): Int {
        return controlDataList.size
    }

    // 更新数据的方法
    fun updateData(newData: List<ControlDataS>) {
        controlDataList.clear()
        controlDataList.addAll(newData)
        notifyDataSetChanged()
    }

    // 删除某项数据
    fun removeItem(position: Int) {
        if (position >= 0 && position < controlDataList.size) {
            controlDataList.removeAt(position)
            notifyItemRemoved(position)

            // 使用 notifyItemRangeChanged 更新受影响的后续项
            notifyItemRangeChanged(position, controlDataList.size)
        }
    }


    // 获取数据列表
    fun getDataList(): List<ControlDataS> {
        return controlDataList
    }
}
