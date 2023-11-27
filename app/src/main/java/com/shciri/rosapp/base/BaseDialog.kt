package com.shciri.rosapp.base

/**
 *功能：弹窗基类
 *作者：liudz
 *日期：2023年09月22日
 */
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.shciri.rosapp.R
import com.shciri.rosapp.databinding.BaseDialogBinding

class BaseDialog(
    context: Context,
    private val title: String?,
    private val content: String?,
    private val cancelText: String?,
    private val confirmText: String?,
    private val onCancelClick: View.OnClickListener?,
    private val onConfirmClick: View.OnClickListener?,
    private var cancelable: Boolean = false

) : Dialog(context) {
    private lateinit var binding: BaseDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BaseDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(android.R.color.transparent);//去掉白色背景


        binding.dialogTitle.text = title
        binding.dialogContent.text = content
        binding.cancelButton.dialogButtonCancel.text = cancelText
        binding.cancelButton.dialogButtonConfirm.text = confirmText

        binding.cancelButton.dialogButtonCancel.setOnClickListener {
            dismiss()
            onCancelClick?.onClick(it)

        }

        binding.cancelButton.dialogButtonConfirm.setOnClickListener {
            onConfirmClick?.onClick(it)
            dismiss()
        }
        setCanceledOnTouchOutside(cancelable)
    }



    class Builder(private val context: Context) {
        private var title: String? = null
        private var content: String? = null
        private var cancelText: String? = null
        private var confirmText: String? = null
        private var onCancelClick: View.OnClickListener? = null
        private var onConfirmClick: View.OnClickListener? = null
        private var cancelable: Boolean = false


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

        fun setOnConfirmClick(listener: View.OnClickListener?): Builder {
            this.onConfirmClick = listener
            return this
        }

        fun setCanceledOnTouchOutside(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }


        fun build(): BaseDialog {
            return BaseDialog(
                context,
                title,
                content,
                cancelText,
                confirmText,
                onCancelClick,
                onConfirmClick,
                cancelable
            )
        }
    }
}
