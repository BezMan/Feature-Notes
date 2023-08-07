package bez.dev.featurenotes.views.screens.note_detail

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.domain.NoteItem
import bez.dev.featurenotes.databinding.DetailActivityListItemBinding

class DetailListAdapter internal constructor(myListener: OnDetailItemClickListener, myItemTouchHelper: ItemTouchHelper, editMode: Boolean) : ListAdapter<NoteItem, DetailListAdapter.DetailItemHolder>(
    DIFF_CALLBACK
) {

    private var mIsEditMode: Boolean = editMode
    private var touchHelper: ItemTouchHelper = myItemTouchHelper
    private var listener: OnDetailItemClickListener = myListener


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DetailItemHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = DetailActivityListItemBinding.inflate(layoutInflater, viewGroup, false)
        return DetailItemHolder(binding)
    }

    override fun onBindViewHolder(detailItemHolder: DetailItemHolder, position: Int) {
        val currentItem = getItem(position)
        detailItemHolder.bind(currentItem)
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class DetailItemHolder(private val binding: DetailActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: NoteItem) {

            binding.apply {
                detailItemText.text = currentItem.itemText.trim()

                //do regardless of mode
                detailImageDeleteItem.showOnEditMode(mIsEditMode)
                detailImageDragItem.showOnEditMode(mIsEditMode)
                detailItemText.showItemIsDone(currentItem.isDone)

                detailItemText.setOnLongClickListener {
                    listener.onDetailItemLongClick(currentItem.itemText, position)
                }

                if (mIsEditMode) { //ONLY edit mode
                    detailItemText.setTextColor(ContextCompat.getColor(listener as Context, R.color.black))

                    detailItemText.setOnClickListener {
                        listener.onDetailItemClick(currentItem, position)
                    }
                    detailImageDeleteItem.setOnClickListener {
                        listener.onDeleteItemClick(position)
                    }
                    detailImageDragItem.setOnTouchListener { _, event ->
                        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                            touchHelper.startDrag(this@DetailItemHolder)
                        }
                        false
                    }
                } else {  //NOT edit mode
                    detailItemText.setTextColor(ContextCompat.getColor(listener as Context, R.color.gray))

                    detailItemText.setOnClickListener {
                        listener.onDetailItemClickToggleDone(!currentItem.isDone, position)
                    }
                }

            }

        }
    }


    interface OnDetailItemClickListener {
        fun onDetailItemClickToggleDone(isItemDone: Boolean, position: Int)
        fun onDetailItemClick(noteItem: NoteItem, position: Int)
        fun onDetailItemLongClick(text: String, position: Int): Boolean
        fun onDeleteItemClick(position: Int)
    }


    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NoteItem>() {
            override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
                return false
            }

            override fun areContentsTheSame(oldNote: NoteItem, newNote: NoteItem): Boolean {
                return false
            }
        }
    }


}

private fun TextView.showItemIsDone(done: Boolean) {
    val sp = SpannableString(text)
    if (done) {
        sp.setSpan(StrikethroughSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = sp
}


fun View.showOnEditMode(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
