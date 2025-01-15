package com.shciri.rosapp.ui.control

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.toast.Toaster
import com.shciri.rosapp.R
import com.shciri.rosapp.RCApplication
import com.shciri.rosapp.adapter.ControlAdapter
import com.shciri.rosapp.adapter.ControlDataAdapter
import com.shciri.rosapp.base.BaseFragment
import com.shciri.rosapp.databinding.FragmentHandControlBinding
import com.shciri.rosapp.dmros.client.RosInit
import com.shciri.rosapp.dmros.tool.VideoImageEvent
import com.shciri.rosapp.rosdata.ControlDataS
import com.shciri.rosapp.rosdata.RowData
import com.shciri.rosapp.ui.view.VerticalSeekBar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import src.com.jilk.ros.Topic
import src.com.jilk.ros.message.custom.request.ImageMessage
import src.com.jilk.ros.message.custom.request.SimplerResponse
import src.com.jilk.ros.message.requestparam.Modalities
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class HandControlFragment : BaseFragment() {
    private lateinit var binding: FragmentHandControlBinding
    private var selectedOption = "left_hand"

    private val angleEdits = mutableListOf<EditText>()
    private val speedEdits = mutableListOf<EditText>()
    private val strengthEdits = mutableListOf<EditText>()

    // 示例：设置不同的数据到 EditTexts
    private var angleValues = arrayOf(100, 200, 300, 400, 500, 600) // 示例角度值
    private var speedValues = arrayOf(50, 60, 70, 80, 90, 100)      // 示例速度值
    private var strengthValues = arrayOf(10, 20, 30, 40, 50, 60)    // 示例力度值

    private var t_mode: Byte = 0
    private var isZoomed = false  // 用于判断是否已经放大

    private var controlDataList = mutableListOf<ControlDataS>()

    private lateinit var controlDataAdapter: ControlDataAdapter
    private var timeInterval = 1000

    private lateinit var seekBars: List<VerticalSeekBar>

    private var selectOne = "raw"
    private var selectTwo = "raw"
    private var selectThree = "raw"

    // 全局变量，用于记录上次发送的时间
    private var lastSentTime = 0L
    private val SEND_INTERVAL = 300 // 发送间隔，单位为毫秒


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHandControlBinding.inflate(inflater, container, false)
        initData()
        initView()
        return binding.root
    }

    private fun initData() {

        // 设置 SeekBar 最大值
        seekBars = listOf(
            binding.progressDa1,
            binding.progressDa,
            binding.progressEr,
            binding.progressSan,
            binding.progressSi,
            binding.progressWu,
        )

        seekBars.forEach { it.setMax(2000) }

        EventBus.getDefault().register(this)

        // 提取公共的监听器逻辑
        fun createSeekBarListener(index: Int): VerticalSeekBar.OnSeekBarChangeListener {
            return object : VerticalSeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: VerticalSeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    // 更新角度值并调用更新
                    angleValues[index] = progress
                    setDataToEditTexts(angleValues, speedValues, strengthValues)
                    sendMessage(1)

//                    // 当前时间
//                    val currentTime = System.currentTimeMillis()
//
//                    // 检查距离上次发送是否超过设定间隔
//                    if (currentTime - lastSentTime >= SEND_INTERVAL) {
//                        sendMessage(1)
//                        lastSentTime = currentTime // 更新上次发送时间
//                    }
                }

                override fun onStopTrackingTouch(seekBar: VerticalSeekBar) {
                    angleValues[index] = seekBar.getNowProgress()
                    setDataToEditTexts(angleValues, speedValues, strengthValues)
                }
            }
        }

        // 绑定监听器到每个 SeekBar
        seekBars.forEachIndexed { index, seekBar ->
            seekBar.setOnSeekBarChangeListener(createSeekBarListener(5 - index))
        }

//        binding.radioLeft.isSelected = true

        val tableLayout = binding.fingerTable

// 表头
        val headerTitles = arrayOf(
            "", getString(R.string.pinky), getString(R.string.ring_finger),
            getString(R.string.middle_finger), getString(R.string.index_finger),
            getString(R.string.thumb_bend), getString(R.string.thumb_rotate)
        )
        val headerRow = TableRow(context)
        for (title in headerTitles) {
            val textView = TextView(context).apply {
                text = title
                textSize = 25f
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
            }
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)

// 行标签
        val rowLabels = arrayOf(
            getString(R.string.angle),
            getString(R.string.speed),
            getString(R.string.intensity)
        )
        val editTextLists = listOf(angleEdits, speedEdits, strengthEdits)

// 动态生成每一行
        for ((index, label) in rowLabels.withIndex()) {
            val tableRow = TableRow(context)

            // 左侧标签
            val labelTextView = TextView(context).apply {
                text = label
                textSize = 25f
                layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
            }
            tableRow.addView(labelTextView)

            // 动态生成 EditText
            for (i in 1 until headerTitles.size) {
                val editText = EditText(context).apply {
                    hint = "0-2000"
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setPadding(16, 16, 16, 16)
                    setBackgroundResource(R.drawable.table_cell_border)
                    layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
                }
                // 将 EditText 添加到对应的列表
                editTextLists[index].add(editText)
                tableRow.addView(editText)
            }
            tableLayout.addView(tableRow)
        }




        setDataToEditTexts(angleValues, speedValues, strengthValues)


        // 设置选项数据
        val itemMode = listOf(
            getString(R.string.position_mode),
            getString(R.string.servo_mode),
            getString(R.string.velocity_mode),
            getString(R.string.force_control_mode)
        )
        binding.spinnerMode.setItems(itemMode)
        // 为 Spinner 设置选项选择监听器
        binding.spinnerMode.setOnItemSelectedListener { _, position, _, _ ->
            // 根据选项设置 t_mode 的值
            t_mode = when (position) {
                0 -> 0  // 定位模式
                1 -> 1  // 伺服模式
                2 -> 2  // 速度模式
                3 -> 3  // 力控模式
                else -> 0 // 默认值
            }
        }

        // 设置选项数据
        val items = listOf("left", "right", "all")
        binding.spinnerDirection.setItems(items)

        // 设置选项选择监听器
        binding.spinnerDirection.setOnItemSelectedListener { _, position, _, _ ->
            // 根据 position 执行不同操作
            selectedOption = when (position) {
                0 -> "left_hand"
                1 -> "right_hand"
                2 -> "all"
                else -> "left_hand"
            }
        }


        // 设置选项数据
        val itemSOne = listOf("raw", "deformation", "normal", "shear")
        binding.spinnerModeOne.setItems(itemSOne)
        binding.spinnerModeTwo.setItems(itemSOne)
        binding.spinnerModeThree.setItems(itemSOne)

        // 设置选项选择监听器
        binding.spinnerModeOne.setOnItemSelectedListener { _, position, _, _ ->
            // 根据 position 执行不同操作
            selectOne = when (position) {
                0 -> "raw"
                1 -> "deformation"
                2 -> "normal"
                else -> "shear"
            }
        }

        binding.spinnerModeTwo.setOnItemSelectedListener { _, position, _, _ ->
            // 根据 position 执行不同操作
            selectTwo = when (position) {
                0 -> "raw"
                1 -> "deformation"
                2 -> "normal"
                else -> "shear"
            }
        }

        binding.spinnerModeThree.setOnItemSelectedListener { _, position, _, _ ->
            // 根据 position 执行不同操作
            selectThree = when (position) {
                0 -> "raw"
                1 -> "deformation"
                2 -> "normal"
                else -> "shear"
            }
        }



        getNowHandData()

    }

    // 设置不同数据到 EditTexts
    private fun setDataToEditTexts(
        angleValues: Array<Int>,
        speedValues: Array<Int>,
        strengthValues: Array<Int>
    ) {
        for ((index, editText) in angleEdits.withIndex()) {
            editText.setText(angleValues[index].toString())
        }
        for ((index, editText) in speedEdits.withIndex()) {
            editText.setText(speedValues[index].toString())
        }
        for ((index, editText) in strengthEdits.withIndex()) {
            editText.setText(strengthValues[index].toString())
        }
    }

    // 获取数据并转换为 Int 数组
    private fun getDataFromEditTexts(editTextList: List<EditText>): IntArray {
        return editTextList.map { editText ->
            val text = editText.text.toString()
            val number = text.toIntOrNull() ?: 0
            if (number in 0..65534) number else 0 // Replace 65535 or other unwanted values with 0
        }.toIntArray()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {

        /**
         * 发送操作数据
         */
        binding.tvSend.setOnClickListener {
            sendMessage(t_mode)
        }

        /**
         * 获取实时信息
         */
        binding.tvUp.setOnClickListener { getNowHandData() }
        /**
         * 设置图像参数
         */
        binding.tvUpImageS.setOnClickListener {
            publishModalitiesControl()
        }

        binding.returnLl.setOnClickListener { Navigation.findNavController(it).navigateUp() }


        //缩放
        binding.btnZoom.setOnClickListener {
            if (isZoomed) {
                animateZoom(binding.constraintHandControl, false)
                binding.btnZoom.text = getString(R.string.enlarge)
            } else {
                animateZoom(binding.constraintHandControl, true)
                binding.btnZoom.text = getString(R.string.narrow)
            }
            isZoomed = !isZoomed
        }

        val dataList = mutableListOf<RowData>()
        val adapter = ControlAdapter(dataList)
        binding.recyclerControl.layoutManager = LinearLayoutManager(context)
        binding.recyclerControl.adapter = adapter

        //添加操作
        binding.btnAdd.setOnClickListener {
            if (binding.editControlInterval.text.toString().isNotEmpty()) {
                timeInterval = binding.editControlInterval.text.toString().toInt()
            }
            val newData = RowData(
                getDataFromEditTexts(angleEdits).toTypedArray(),
                getDataFromEditTexts(speedEdits).toTypedArray(),
                getDataFromEditTexts(strengthEdits).toTypedArray(),
                4,
                timeInterval
            )
            adapter.addData(newData)
        }

        //保存
        binding.btnSave.setOnClickListener {
            // 保存到SharedPreferences
            if (!TextUtils.isEmpty(binding.editControlName.text.toString()) && adapter.getDataList()
                    .isNotEmpty()
            ) {
                val controlData =
                    ControlDataS(adapter.getDataList(), binding.editControlName.text.toString())
                controlDataList.add(controlData)
                saveDataToPreferences(controlDataList)
                dataList.clear()
                adapter.notifyDataSetChanged()
                controlDataAdapter.notifyDataSetChanged()
                binding.editControlName.setText("")
                loadData()
            } else {
                toast(R.string.please_input_name_or_data)
            }
        }

        binding.btnTest.setOnClickListener {
            val controlDataTest =
                ControlDataS(adapter.getDataList(), "test")
            executeControlData(controlDataTest)
        }

        binding.recyclerList.layoutManager = LinearLayoutManager(context)
        controlDataAdapter = ControlDataAdapter(controlDataList, onExecuteClick = { controlDataS ->
            executeControlData(controlDataS)
        }, onDeleteClick = { position ->
            deleteControlData(position)
        })
        binding.recyclerList.adapter = controlDataAdapter
        loadData()

        binding.tvTranScribe.setOnClickListener {
            toggleVisibility(binding.lineTranscribe)
        }

        binding.tvExpendControlList.setOnClickListener {
            toggleVisibility(binding.lineControlList)
        }

    }

    private fun toggleVisibility(targetView: View) {
        val isHidden = targetView.visibility == View.GONE
        binding.lineTranscribe.visibility = View.GONE
        binding.lineControlList.visibility = View.GONE

        if (isHidden) {
            targetView.visibility = View.VISIBLE
            binding.linOne.visibility = View.GONE
            binding.linTwo.visibility = View.GONE
        } else {
            binding.linOne.visibility = View.VISIBLE
            binding.linTwo.visibility = View.VISIBLE
        }
    }

    private fun animateZoom(constraintLayout: ConstraintLayout, isZoomIn: Boolean) {
        // 获取屏幕宽高
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        // 布局的宽高
        val layoutWidth = constraintLayout.width
        val layoutHeight = constraintLayout.height

        // 计算放大后的宽高
        val scaleFactor = if (isZoomIn) 2.0f else 1f
        val scaledWidth = layoutWidth * scaleFactor
        val scaledHeight = layoutHeight * scaleFactor

        // 如果是放大，计算平移偏移量，保持中心
        val translationX = if (isZoomIn) (screenWidth - scaledWidth) / 2 else 0f
        val translationY = if (isZoomIn) (screenHeight - scaledHeight) / 2 else 0f

        // 设置 pivotX 和 pivotY 为布局自身中心
        constraintLayout.pivotX = layoutWidth / 2f
        constraintLayout.pivotY = layoutHeight / 2f

        // 缩放动画
        val scaleX = ObjectAnimator.ofFloat(constraintLayout, "scaleX", scaleFactor)
        val scaleY = ObjectAnimator.ofFloat(constraintLayout, "scaleY", scaleFactor)

        // 平移动画，放大时居中，缩小时回到原位
        val translateX = ObjectAnimator.ofFloat(constraintLayout, "translationX", translationX)
        val translateY = ObjectAnimator.ofFloat(constraintLayout, "translationY", translationY)

        // 创建动画集合并启动
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, translateX, translateY)
        animatorSet.duration = 300 // 动画时长
        animatorSet.start()
    }


    private fun getNowHandData() {
        if (rosServiceCaller != null && RosInit.isConnect) {
            rosServiceCaller.callGetHandParmer(selectedOption, handler = {

                // 检查并替换异常值
                val angleArrayE =
                    it.angle_array.map { angle -> if (angle == 65535) 0 else angle }.toIntArray()
                val speedArrayE =
                    it.speed_array.map { speed -> if (speed == 65535) 0 else speed }.toIntArray()
                val strengthArrayE =
                    it.strength_array.map { strength -> if (strength == 65535) 0 else strength }
                        .toIntArray()
                setDataToEditTexts(
                    angleArrayE.toTypedArray(),
                    speedArrayE.toTypedArray(),
                    strengthArrayE.toTypedArray()
                )
                angleValues = it.angle_array.toTypedArray()
                speedValues = it.speed_array.toTypedArray()
                strengthValues = it.strength_array.toTypedArray()

                val angleArray = it.angle_array
                val safeLength = minOf(seekBars.size, angleArray.size)
                for (i in 0 until safeLength) {
                    seekBars[safeLength - 1 - i].setNowProgress(angleArray[i])
                }
            })

        }
    }

    fun sendMessage(mode: Byte) {
        if (rosServiceCaller != null && RosInit.isConnect) {
            rosServiceCaller.callHandService(
                selectedOption,
                angleValues.toIntArray(),
                speedValues.toIntArray(),
                strengthValues.toIntArray(),
                mode,
                handler = {
                    if (it.ret) {
                        // 获取数据，转换为 Int 数组
                        angleValues = getDataFromEditTexts(angleEdits).toTypedArray()
                        speedValues = getDataFromEditTexts(speedEdits).toTypedArray()
                        strengthValues = getDataFromEditTexts(strengthEdits).toTypedArray()
                        val angleArray = angleValues
                        val safeLength = minOf(seekBars.size, angleArray.size)
                        for (i in 0 until safeLength) {
                            seekBars[safeLength - 1 - i].setNowProgress(angleArray[i])
                        }

                    } else {
                        toast(R.string.subscribe_fail)
                    }
                });
        } else {
            toast(R.string.subscribe_fail)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: Any) {
        when (event) {
            is VideoImageEvent -> {
                val list = event.imageList.images
                for (i in list.indices) {
                    when (i) {
                        0 -> handleImageEvent(list[i], binding.imageOne)
                        1 -> handleImageEvent(list[i], binding.imageTwo)
                        2 -> handleImageEvent(list[i], binding.imageThree)
                    }
                }

            }

            else -> {
                Log.e("EventError", "Unhandled event type: ${event::class.java.simpleName}")
            }
        }
    }

    // Centralized method for handling image events
    private fun handleImageEvent(imageMessage: ImageMessage, imageView: ImageView) {
        val bitmap = ImageUtils.convertBGR8ToBitmap(imageMessage)

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            Log.e(
                "ImageError",
                "Failed to convert BGR8 image data to Bitmap for imageView: ${imageView.id}"
            )
        }
    }

    object ImageUtils {
        // Converts BGR8 image data to Bitmap
        fun convertBGR8ToBitmap(imageMessage: ImageMessage): Bitmap? {
            return try {
                // Decode Base64 to byte array
                val decodedBytes = Base64.decode(imageMessage.data, Base64.DEFAULT)
                // Convert byte array to Bitmap
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: IllegalArgumentException) {
                Log.e("ImageUtils", "Base64 decoding failed", e)
                null
            }
        }
    }


    // 保存到SharedPreferences，保存List<ControlDataS>
    private fun saveDataToPreferences(dataList: List<ControlDataS>) {
        val sharedPreferences =
            requireContext().getSharedPreferences("ControlData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 转换数据列表为JSON字符串
        val gson = Gson()
        val jsonString = gson.toJson(dataList)

        // 保存JSON字符串到SharedPreferences
        editor.putString("saved_data", jsonString)
        editor.apply()  // 异步保存
    }

    // 从SharedPreferences加载List<ControlDataS>
    private fun loadDataFromPreferences(): List<ControlDataS> {
        val sharedPreferences =
            requireContext().getSharedPreferences("ControlData", Context.MODE_PRIVATE)
        val jsonString = sharedPreferences.getString("saved_data", null)

        return if (jsonString != null) {
            val gson = Gson()
            val type = object : TypeToken<List<ControlDataS>>() {}.type
            gson.fromJson(jsonString, type)
        } else {
            listOf()  // 返回一个空的列表
        }
    }


    // 执行对应的ControlDataS
    @OptIn(DelicateCoroutinesApi::class)
    private fun executeControlData(controlDataS: ControlDataS) {
        // 拿出 dataList 做巡航操作
        val dataList = controlDataS.dataList

        // 启动协程处理
        GlobalScope.launch {
            if (rosServiceCaller != null) {
                processDataListSequentially(dataList, 0)
            } else {
                toast(R.string.ros_connect_fail)
            }

        }
    }

    // 递归处理 dataList，并增加超时机制
    private suspend fun processDataListSequentially(dataList: List<RowData>, index: Int) {
        // 如果已经处理完所有数据，结束递归
        if (index >= dataList.size) {
            toast(R.string.control_success)
            return
        }

        // 获取当前要处理的 RowData
        val rowData = dataList[index]

        // 准备所需的参数
        val selectedOption = selectedOption
        val angleValues = rowData.angleArray
        val speedValues = rowData.speedArray
        val strengthValues = rowData.strengthArray
        val t_mode = rowData.t_mode // 假设 rowData 有这个属性

        try {
            // 设置超时时间为 30 秒
            withTimeout(30_000L) {
                val result = callHandServiceSuspended(
                    selectedOption,
                    angleValues.toIntArray(),
                    speedValues.toIntArray(),
                    strengthValues.toIntArray(),
                    4
                )

                if (result.ret) {
                    // 当前操作成功，处理下一个
                    processDataListSequentially(dataList, index + 1)
                } else {
                    // 操作失败，提示错误
                    toast(R.string.subscribe_fail)
                }
            }
        } catch (e: TimeoutCancellationException) {
            // 超时处理
            toast(R.string.control_timeout)
        }
    }

    // 将 HandService 调用转换为挂起函数 (suspend function)
    private suspend fun callHandServiceSuspended(
        selectedOption: String,
        angleValues: IntArray,
        speedValues: IntArray,
        strengthValues: IntArray,
        t_mode: Byte
    ): SimplerResponse = suspendCoroutine { continuation ->
        if (rosServiceCaller != null && RosInit.isConnect) {
            rosServiceCaller.callHandService(
                selectedOption,
                angleValues,
                speedValues,
                strengthValues,
                t_mode,
                handler = {
                    continuation.resume(it) // 将结果返回给协程
                }
            )
        }else{
            toast(R.string.subscribe_fail)
        }


    }


    // 删除对应的项
    private fun deleteControlData(position: Int) {
        if (position >= 0 && position < controlDataList.size) {
            // 从数据列表中移除项
            controlDataAdapter.removeItem(position)
            // 更新保存的数据
            saveDataToPreferences(controlDataAdapter.getDataList())  // 直接获取适配器的最新数据
        }
    }

    // 发布消息
    private fun publishModalitiesControl() {
        if ((requireActivity().application as RCApplication).rosClient != null) {
            val modalitiesTopic: Topic<Modalities> =
                Topic(
                    "/get_images", Modalities::class.java,
                    (requireActivity().application as RCApplication).rosClient
                )
            modalitiesTopic.advertise()

            // 创建并发送消息
            val modalities = Modalities()
            modalities.data = arrayOf(selectOne, selectTwo, selectThree)
            modalitiesTopic.publish(modalities)
        } else {
            toast(R.string.ros_connect_fail)
        }
    }


    // 加载数据并更新RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    private fun loadData() {
        val savedData = loadDataFromPreferences()
        controlDataAdapter.updateData(savedData)  // 更新适配器中的数据
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}

