package bez.dev.featurenotes.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import bez.dev.featurenotes.R
import kotlinx.android.synthetic.main.detail_activity_dialog_priority.*

class DetailPriorityDialog(context: Context, private val mPriority: Int) : Dialog(context) {

    private var myListener: OnPrioritySaveClickListener? = context as OnPrioritySaveClickListener


    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.detail_activity_dialog_priority)

        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        numberPicker.wrapSelectorWheel = false //non-circular
        numberPicker.minValue = 1
        numberPicker.maxValue = 5
        numberPicker.value = mPriority

        savePriorityBtn.setOnClickListener {
            val newPriority = numberPicker.value
            myListener?.onPrioritySaveBtnClick(newPriority)
            dismiss()
        }
    }

    interface OnPrioritySaveClickListener {
        fun onPrioritySaveBtnClick(newPriority: Int)
    }
}