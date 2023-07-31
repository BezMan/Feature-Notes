package bez.dev.featurenotes.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.NoteItem

class DetailListAdapter internal constructor(myListener: OnDetailItemClickListener, myItemTouchHelper: ItemTouchHelper, editMode: Boolean) : ListAdapter<NoteItem, DetailListAdapter.DetailItemHolder>(DIFF_CALLBACK) {

    private var mIsEditMode: Boolean = editMode
    private var touchHelper: ItemTouchHelper = myItemTouchHelper
    private var listener: OnDetailItemClickListener = myListener


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DetailItemHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.detail_activity_list_item, viewGroup, false)
        return DetailItemHolder(itemView)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(detailItemHolder: DetailItemHolder, position: Int) {
        val currentItem = getItem(position)

        detailItemHolder.apply {
            itemText.text = currentItem.itemText.trim()

            //do regardless of mode
            deleteItem.showOnEditMode(mIsEditMode)
            dragItem.showOnEditMode(mIsEditMode)
            itemText.showItemIsDone(currentItem.isDone)

            itemText.setOnLongClickListener {
                listener.onDetailItemLongClick(currentItem.itemText, position)
            }

            if (mIsEditMode) { //ONLY edit mode
                itemText.setTextColor(ContextCompat.getColor(listener as Context, R.color.black))

                itemText.setOnClickListener {
                    listener.onDetailItemClick(currentItem, position)
                }
                deleteItem.setOnClickListener {
                    listener.onDeleteItemClick(position)
                }
                dragItem.setOnTouchListener { _, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(detailItemHolder)
                    }
                    false
                }
            } else {  //NOT edit mode
                itemText.setTextColor(ContextCompat.getColor(listener as Context, R.color.gray))

                itemText.setOnClickListener {
                    listener.onDetailItemClickToggleDone(!currentItem.isDone, position)
                }
            }
        }

    }

    inner class DetailItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val deleteItem: ImageView = itemView.detail_image_delete_item
        internal val itemText: TextView = itemView.detail_item_text
        internal val dragItem: ImageView = itemView.detail_image_drag_item
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
