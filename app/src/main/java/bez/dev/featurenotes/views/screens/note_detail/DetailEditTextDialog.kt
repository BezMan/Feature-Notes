package bez.dev.featurenotes.views.screens.note_detail

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import bez.dev.featurenotes.data.domain.NoteItem
import bez.dev.featurenotes.databinding.DetailActivityDialogEditTextBinding
import bez.dev.featurenotes.misc.Utils

class DetailEditTextDialog(context: Context, private val noteItem: NoteItem, myPosition: Int = 0) : Dialog(context) {

    private var myListener: OnItemSaveClickListener = context as OnItemSaveClickListener
    private var isNewItem: Boolean = false
    private var position: Int = myPosition

    private lateinit var _binding: DetailActivityDialogEditTextBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = DetailActivityDialogEditTextBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initUI()

        if (noteItem.itemText.isEmpty()) {
            isNewItem = true //is new item?
        } else {
            _binding.dialogEditText.append(noteItem.itemText)
        }

        _binding.dialogSaveTextBtn.setOnClickListener {
            saveMe()
            dismiss()
        }
    }

    private fun initUI() {
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        Utils.showKeyboard(window)
    }

    fun saveMe() {
        val newNoteItem = NoteItem(_binding.dialogEditText.text.toString().trim(), noteItem.isDone)
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
