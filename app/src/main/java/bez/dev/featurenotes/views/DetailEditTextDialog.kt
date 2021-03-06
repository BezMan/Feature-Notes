package bez.dev.featurenotes.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.NoteItem
import bez.dev.featurenotes.misc.Utils
import kotlinx.android.synthetic.main.detail_activity_dialog_edit_text.*

class DetailEditTextDialog(context: Context, listener: OnItemSaveClickListener, private val noteItem: NoteItem, myPosition: Int = 0) : Dialog(context) {

    private var myListener: OnItemSaveClickListener = listener
    private var isNewItem: Boolean = false
    private var position: Int = myPosition

    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.detail_activity_dialog_edit_text)

        initUI()

        if (noteItem.itemText.isEmpty()) {
            isNewItem = true //is new item?
        } else {
            dialogEditText.append(noteItem.itemText)
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
        val newNoteItem = NoteItem(dialogEditText.text.toString().trim(), noteItem.isDone)
        myListener.onTextSaveDialogBtnClick(newNoteItem, position, isNewItem)
    }


    override fun dismiss() {
        Utils.hideKeyboard(context, window)
        super.dismiss()
    }

    interface OnItemSaveClickListener {
        fun onTextSaveDialogBtnClick(noteItem: NoteItem, position: Int, isAddedItem: Boolean)
    }
}
