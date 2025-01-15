package com.shciri.rosapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.shciri.rosapp.R
import com.shciri.rosapp.rosdata.RowData

class ControlAdapter(private val dataList: MutableList<RowData>) :
    RecyclerView.Adapter<ControlAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 每个item中的18个EditText
        val numberEditTexts: List<EditText> = listOf(
            view.findViewById(R.id.editTextNumber1),
            view.findViewById(R.id.editTextNumber2),
            view.findViewById(R.id.editTextNumber3),
            view.findViewById(R.id.editTextNumber4),
            view.findViewById(R.id.editTextNumber5),
            view.findViewById(R.id.editTextNumber6),
            view.findViewById(R.id.editTextNumber7),
            view.findViewById(R.id.editTextNumber8),
            view.findViewById(R.id.editTextNumber9),
            view.findViewById(R.id.editTextNumber10),
            view.findViewById(R.id.editTextNumber11),
            view.findViewById(R.id.editTextNumber12),
            view.findViewById(R.id.editTextNumber13),
            view.findViewById(R.id.editTextNumber14),
            view.findViewById(R.id.editTextNumber15),
            view.findViewById(R.id.editTextNumber16),
            view.findViewById(R.id.editTextNumber17),
            view.findViewById(R.id.editTextNumber18)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val rowData = dataList[position]

        // 设置前6个EditText为角度数组值
        for (i in 0 until 6) {
            holder.numberEditTexts[i].setText(rowData.angleArray[i].toString())
        }

        // 设置中间6个EditText为速度数组值
        for (i in 6 until 12) {
            holder.numberEditTexts[i].setText(rowData.speedArray[i - 6].toString())
        }

        // 设置后6个EditText为力度数组值
        for (i in 12 until 18) {
            holder.numberEditTexts[i].setText(rowData.strengthArray[i - 12].toString())
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun getDataList(): MutableList<RowData> {
        return dataList
    }

    // 添加一条数据
    fun addData(newData: RowData) {
        dataList.add(newData)
        notifyItemInserted(dataList.size - 1)
    }
}
