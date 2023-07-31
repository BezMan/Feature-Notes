package bez.dev.featurenotes.views

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.databinding.MainActivityListItemBinding

class MainListAdapter internal constructor(context: OnItemClickListener) : ListAdapter<Note, MainListAdapter.NoteHolder>(DIFF_CALLBACK) {
    private var listener: OnItemClickListener = context

    internal fun getNoteAt(position: Int): Note {
        return getItem(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoteHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = MainActivityListItemBinding.inflate(layoutInflater, viewGroup, false)
        return NoteHolder(binding)
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
        noteHolder.bind(currentNote)
    }


    interface OnItemClickListener {
        fun onNoteItemTextClick(note: Note)
        fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: NoteHolder)
        fun onToggleNotificationClick(note: Note, isChecked: Boolean)
    }

    inner class NoteHolder(private val binding: MainActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentNote: Note) {
            binding.apply {
                textViewTitle.text = currentNote.title
                textViewPriority.text = currentNote.priority.toString()
                checkboxToggleButton.isChecked = currentNote.isNotification

                textViewTitle.setOnClickListener {
                    listener.onNoteItemTextClick(currentNote)
                }
                overflowImageNoteItem.setOnClickListener {
                    listener.onNoteItemOverflowClick(currentNote, overflowImageNoteItem, this@NoteHolder)
                }
                checkboxToggleButton.setOnCheckedChangeListener { _, isChecked ->
                    listener.onToggleNotificationClick(currentNote, isChecked)
                }
            }
        }
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
