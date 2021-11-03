package bez.dev.featurenotes.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import kotlinx.android.synthetic.main.main_activity_list_item.view.*

class MainListAdapter internal constructor(context: OnItemClickListener) : ListAdapter<Note, MainListAdapter.NoteHolder>(DIFF_CALLBACK) {
    private var listener: OnItemClickListener = context

    internal fun getNoteAt(position: Int): Note {
        return getItem(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoteHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.main_activity_list_item, viewGroup, false)
        return NoteHolder(itemView)
    }


    //methods preventing click on an item affecting different items//
    override fun getItemViewType(position: Int): Int {
        return getNoteAt(position).id.toInt()
    }

    override fun getItemId(position: Int): Long {
        return getNoteAt(position).id
    }
    //methods preventing click on an item affecting different items//


    override fun onBindViewHolder(noteHolder: NoteHolder, position: Int) {
        val currentNote = getNoteAt(position)

        noteHolder.apply {
            tvTitle.text = currentNote.title
            tvPriority.text = currentNote.priority.toString()
            checkboxToggleNotification.isChecked = currentNote.isNotification

            tvTitle.setOnClickListener {
                listener.onNoteItemTextClick(currentNote)
            }
            overflow.setOnClickListener {
                listener.onNoteItemOverflowClick(currentNote, overflow, noteHolder)
            }
            checkboxToggleNotification.setOnCheckedChangeListener { _, isChecked ->
                listener.onToggleNotificationClick(currentNote, isChecked)
            }
        }

    }


    interface OnItemClickListener {
        fun onNoteItemTextClick(note: Note)
        fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: NoteHolder)
        fun onToggleNotificationClick(note: Note, isChecked: Boolean)
    }

    inner class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val tvTitle: TextView = itemView.text_view_title
        internal val tvPriority: TextView = itemView.text_view_priority
        internal val overflow: ImageView = itemView.overflow_image_note_item
        internal val checkboxToggleNotification: CheckBox = itemView.checkbox_toggle_button
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.id == newNote.id
            }

            override fun areContentsTheSame(oldNote: Note, newNote: Note): Boolean {
                return oldNote.title == newNote.title
                        && oldNote.priority == newNote.priority
                        && oldNote.isNotification == newNote.isNotification
                        && oldNote.color == newNote.color
                        && oldNote.isArchived == newNote.isArchived
                        && oldNote.timeCreated == newNote.timeCreated
                        && oldNote.timeModified == newNote.timeModified
            }
        }
    }
}
