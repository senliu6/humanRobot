package com.shciri.rosapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.shciri.rosapp.databinding.DialogInputBinding
import com.shciri.rosapp.utils.regex.LimitInputTextWatcher


/**
 * 功能：输入弹窗
 * @author ：liudz
 * 日期：2023年10月17日
 */
class InputDialog(
    context: Context,
    private val title: String?,
    private val content: String?,
    private val confirmText: String?,
    private val cancelText: String?,
    private val onCancelClick: View.OnClickListener?,
    private val onConfirmClick: InputDialogListener?,
    private var cancelable: Boolean = false,
    private val regex: String?,
    private val maxLength: Int,
    private var editShow: Boolean = true
) : Dialog(context) {
    private lateinit var binding: DialogInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent);//去掉白色背景

        // 获取窗口参数
        val layoutParams = window?.attributes

        // 设置宽度，例如设置为屏幕宽度的一部分（占满屏幕宽度的比例）
        layoutParams?.width =
            (context.resources.displayMetrics.widthPixels * 0.3).toInt() // 设置为屏幕宽度的30%

        // 应用窗口参数
        window?.attributes = layoutParams


        binding.tvTitle.text = title
        binding.tvContent.text = content
        if (null != cancelText) {
            binding.cancelButton.dialogButtonCancel.text = cancelText
        }
        if (null != confirmText) {
            binding.cancelButton.dialogButtonConfirm.text = confirmText
        }

        binding.cancelButton.dialogButtonCancel.setOnClickListener {
            hideSoftKeyboard()
            onCancelClick?.onClick(it)
            dismiss()
        }

        binding.cancelButton.dialogButtonConfirm.setOnClickListener {
            hideSoftKeyboard()
            onConfirmClick?.onConfirmClicked(binding.editInput.text.toString())

        }
        val filters = arrayOf<InputFilter>(LengthFilter(maxLength))
        binding.editInput.filters = filters
        setCanceledOnTouchOutside(cancelable)
        binding.editInput.isVisible = editShow
    }

    class Builder(private val context: Context) {
        private var title: String? = null
        private var content: String? = null
        private var confirmText: String? = null
        private var cancelText: String? = null
        private var onCancelClick: View.OnClickListener? = null
        private var onConfirmClick: InputDialogListener? = null
        private var cancelable: Boolean = false
        private var regex: String? = null
        private var maxLength: Int = 15
        private var editShow: Boolean = true

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setContent(content: String?): Builder {
            this.content = content
            return this
        }

        fun setCancelText(cancelText: String?): Builder {
            this.cancelText = cancelText
            return this
        }

        fun setConfirmText(confirmText: String?): Builder {
            this.confirmText = confirmText
            return this
        }

        fun setOnCancelClick(listener: View.OnClickListener?): Builder {
            this.onCancelClick = listener
            return this
        }

        fun setOnConfirmClick(listener: InputDialogListener): Builder {
            this.onConfirmClick = listener
            return this
        }

        fun setCanceledOnTouchOutside(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setTextChangedListener(regex: String?): Builder {
            this.regex = regex
            return this
        }

        fun setMaxLength(maxLength: Int): Builder {
            this.maxLength = maxLength
            return this
        }

        fun setEditShow(editShow: Boolean): Builder {
            this.editShow = editShow
            return this
        }

        fun build(): InputDialog {
            return InputDialog(
                context,
                title,
                content,
                confirmText,
                cancelText,
                onCancelClick,
                onConfirmClick,
                cancelable,
                regex,
                maxLength,
                editShow
            )
        }

    }


    interface InputDialogListener {
        fun onConfirmClicked(inputText: String)
    }

    private fun showSoftKeyboard() {
        // 获取输入法管理器
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 显示软键盘
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    private fun hideSoftKeyboard() {
        // 获取输入法管理器
        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

        // 隐藏软键盘
        imm?.hideSoftInputFromWindow(binding.editInput.windowToken, 0)
    }


}