package bez.dev.featurenotes.views.screens.note_detail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import bez.dev.featurenotes.databinding.DetailActivityDialogPriorityBinding

class DetailPriorityDialog(context: Context, private val mPriority: Int) : Dialog(context) {

    private lateinit var _binding: DetailActivityDialogPriorityBinding
    private var myListener: OnPrioritySaveClickListener? = context as OnPrioritySaveClickListener

    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = DetailActivityDialogPriorityBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        _binding.numberPicker.apply {
            wrapSelectorWheel = false //non-circular
            minValue = 1
            maxValue = 5
            value = mPriority

        }

        _binding.savePriorityBtn.setOnClickListener {
            val newPriority = _binding.numberPicker.value
            myListener?.onPrioritySaveBtnClick(newPriority)
            dismiss()
        }
    }

    interface OnPrioritySaveClickListener {
        fun onPrioritySaveBtnClick(newPriority: Int)
    }
}