package com.shciri.rosapp.ui.dialog

/**
 *功能：等待加载弹窗
 *作者：liudz
 *日期：2023年09月25日
 */
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup.GONE
import android.view.ViewGroup.LayoutParams
import com.shciri.rosapp.databinding.WaitDialogLayoutBinding

class WaitDialog private constructor(
    context: Context,
    private val title: String?,
    private val content: String?,
    private val loadingText: String?,
    private val loadingDurationMillis: Long,
    private val confirmText: String?,
    private val cancelText: String?
) : Dialog(context) {

    private lateinit var binding: WaitDialogLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WaitDialogLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取窗口参数
        val layoutParams = window?.attributes

        // 设置宽度，例如设置为屏幕宽度的一部分（占满屏幕宽度的比例）
        layoutParams?.width = (context.resources.displayMetrics.widthPixels * 0.4).toInt() // 设置为屏幕宽度的40%

        // 应用窗口参数
        window?.attributes = layoutParams

        window?.setBackgroundDrawableResource(android.R.color.transparent);//去掉白色背景

        setCanceledOnTouchOutside(false)

        binding.tvTitle.text = title
        binding.tvContent.text = content
        binding.cancelButton.dialogButtonConfirm.text = confirmText
        binding.cancelButton.dialogButtonCancel.text = cancelText
        binding.tvLoading.text = loadingText


        // 设置确定按钮点击监听器
        binding.cancelButton.dialogButtonConfirm.setOnClickListener {
            onConfirmClickListener?.onClick(it)
            dismiss()
        }

        // 设置取消按钮点击监听器
        binding.cancelButton.dialogButtonCancel.setOnClickListener {
            onCancelClickListener?.onClick(it)
            dismiss()
        }

        // 建议使用 Looper.getMainLooper() 来获取主线程的 Looper
        val handler = Handler(Looper.getMainLooper())

        // 延迟一定时间后停止加载动画
        if (loadingDurationMillis > 0) {
            handler.postDelayed({
                dismiss()
            }, loadingDurationMillis)
        }

        if (TextUtils.isEmpty(binding.cancelButton.dialogButtonCancel.text)){
            binding.cancelButton.dialogButtonCancel.visibility = GONE
        }

        if (TextUtils.isEmpty(binding.cancelButton.dialogButtonConfirm.text)){
            binding.cancelButton.dialogButtonConfirm.visibility = GONE
        }

    }


    private var onConfirmClickListener: View.OnClickListener? = null
    private var onCancelClickListener: View.OnClickListener? = null

    // 设置确定按钮点击监听器
    fun setOnConfirmClickListener(listener: View.OnClickListener?) {
        onConfirmClickListener = listener
    }

    // 设置取消按钮点击监听器
    fun setOnCancelClickListener(listener: View.OnClickListener?) {
        onCancelClickListener = listener
    }

    class Builder(private val context: Context) {
        private var title: String? = null
        private var content: String? = null
        private var loadingText: String? = null
        private var loadingDurationMillis: Long = 0
        private var confirmText: String? = null
        private var cancelText: String? = null

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setLoadingText(loadingText: String?): Builder {
            this.loadingText = loadingText
            return this
        }

        fun setLoadingDurationMillis(loadingDurationMillis: Long): Builder {
            this.loadingDurationMillis = loadingDurationMillis
            return this
        }

        fun setConfirmText(confirmText: String?): Builder {
            this.confirmText = confirmText
            return this
        }

        fun setCancelText(cancelText: String?): Builder {
            this.cancelText = cancelText
            return this
        }

        fun build(): WaitDialog {
            return WaitDialog(
                context,
                title,
                content,
                loadingText,
                loadingDurationMillis,
                confirmText,
                cancelText
            )
        }
    }
}
