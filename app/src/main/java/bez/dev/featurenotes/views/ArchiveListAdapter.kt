package bez.dev.featurenotes.views

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.databinding.ArchiveListItemBinding

class ArchiveListAdapter (context: OnItemClickListener) : ListAdapter<Note, ArchiveListAdapter.NoteHolder>(DIFF_CALLBACK) {
    private var listener: OnItemClickListener = context

    internal fun getNoteAt(position: Int): Note {
        return getItem(position)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NoteHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ArchiveListItemBinding.inflate(layoutInflater, viewGroup, false)
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
        fun onNoteItemUnArchive(note: Note)
        fun onNoteItemOverflowClick(note: Note, overflow: ImageView)
    }

    inner class NoteHolder(private val binding: ArchiveListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentNote: Note){
            binding.apply {
                textViewTitle.text = currentNote.title
                textViewPriority.text = currentNote.priority.toString()

                textViewTitle.setOnClickListener {
                    listener.onNoteItemTextClick(currentNote)
                }

                itemUnarchive.setOnClickListener {
                    listener.onNoteItemUnArchive(currentNote)
                }

                overflowImageNoteItem.setOnClickListener {
                    listener.onNoteItemOverflowClick(currentNote, binding.overflowImageNoteItem)
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
