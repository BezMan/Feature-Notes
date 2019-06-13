package bez.dev.featurenotes.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import bez.dev.featurenotes.R
import bez.dev.featurenotes.misc.Utils
import kotlinx.android.synthetic.main.detail_activity_dialog_edit_text.*

class DetailEditTextDialog(context: Context, listener: OnItemSaveClickListener, private val itemText: String = "", myPosition: Int = 0) : Dialog(context) {

    private var myListener: OnItemSaveClickListener = listener
    private var isAddedItem: Boolean = false
    private var position: Int = myPosition
    private var mContext = context

    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.detail_activity_dialog_edit_text)

        initUI()

        if (itemText.isEmpty()) {
            isAddedItem = true //is new item?
        } else {
            dialogEditText.append(itemText)
        }

        dialogSaveTextBtn.setOnClickListener {
            saveMe()
            dismiss()
        }
    }

    private fun initUI() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        Utils.showKeyboard(window)
    }

    fun saveMe() {
        myListener.onTextSaveDialogBtnClick(dialogEditText.text.toString().trim(), position, isAddedItem)
    }


    override fun dismiss() {
        Utils.hideKeyboard(mContext, window)
        super.dismiss()
    }

    interface OnItemSaveClickListener {
        fun onTextSaveDialogBtnClick(newText: String, position: Int, isAddedItem: Boolean)
    }
}
