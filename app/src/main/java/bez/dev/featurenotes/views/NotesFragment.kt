package bez.dev.featurenotes.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.view_models.RepoViewModel
import bez.dev.featurenotes.views.BaseActivity.Companion.EXTRA_NOTE
import bez.dev.featurenotes.views.BaseActivity.Companion.toggleShowView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.no_notes_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class NotesFragment : Fragment(), MainListAdapter.OnItemClickListener {

    private lateinit var mainListAdapter: MainListAdapter
    private var noteList: List<Note> = ArrayList()
    private lateinit var restorePoint: List<Note>
    private lateinit var mContext: Context

    private var repoViewModel = get<RepoViewModel>()
    private val notificationManager = get<NotificationManager>()


    private val observer = Observer<List<Note>> {
        noteList = it
        refreshUI()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        initNoteViewModel()

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notes_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initUI() {
        //RECYCLER
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
        mainListAdapter = MainListAdapter(this)
        recycler_view.adapter = mainListAdapter

        //FAB
        fab_add_note.setOnClickListener { addNote() }

    }

    private fun initNoteViewModel() {
        repoViewModel.allNotes.observe(this, observer)
    }


    private fun refreshUI() {
        mainListAdapter.submitList(noteList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        no_notes_view.toggleShowView(noteList.isNullOrEmpty())

        //Notification
        notificationManager.updateNotification(noteList)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_add_note -> {
                addNote()
            }
            R.id.main_menu_reset_all_notifications -> {
                resetAllNotifications()
            }
            R.id.main_menu_delete_all_notes -> {
                checkDeleteAllNotes()
            }
            R.id.main_menu_open_image -> {
                val intent = Intent(activity, ImageActivity::class.java)
                startActivity(intent)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    private fun resetAllNotifications() {
        if (noteList.isNotEmpty()) {
            repoViewModel.resetAllNotifications()
        }
    }


    override fun onNoteItemTextClick(note: Note) {
        editNote(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: MainListAdapter.NoteHolder) {
        val popup = PopupMenu(mContext, overflow)
        //Inflating the Popup using xml file
        popup.menuInflater.inflate(R.menu.overflow_note_popup, popup.menu)

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    editNote(note)
                }
                R.id.main_overflow_note_share -> {
                    shareNote(note)
                }
                R.id.main_overflow_note_delete -> {
                    deleteNote(note)
                    showUndoDelete(note)
                }
                R.id.main_overflow_note_notification -> {
                    noteHolder.checkboxToggleNotification.performClick()
                }

            }
            false
        }

        popup.show() //showing popup menu
    }


    private fun deleteNote(note: Note) {
        notificationManager.cancelNotificationById(note.id)
        repoViewModel.delete(note)
    }


    protected fun shareNote(note: Note) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, note.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


    override fun onToggleNotificationClick(note: Note, isChecked: Boolean) {
        if (isChecked != note.isNotification) {
            notificationManager.cancelNotificationById(note.id)

            note.isNotification = isChecked
            repoViewModel.update(note)
        }

    }


    private fun addNote() {
        val intent = Intent(activity, DetailActivity::class.java)
        startActivity(intent)
    }

    private fun editNote(note: Note) {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        startActivity(intent)
    }


    private fun checkDeleteAllNotes() {
        if (noteList.isNotEmpty()) {
            restorePoint = noteList

            val builder = AlertDialog.Builder(mContext)
            builder.setMessage("All notes will be deleted.. \n Are you sure?")
                    .setPositiveButton("Yes") { dialog, id ->
                        repoViewModel.deleteAllNotes()
                        showUndoDeleteAllNotes(restorePoint)
                    }
                    .setNegativeButton("Cancel") { dialog, id ->
                        dialog.dismiss()
                    }

            val alert = builder.create()
            alert.show()
        }
    }


    private fun showUndoDelete(note: Note) {
        val snack = Snackbar.make(notes_layout, note.title + " - note deleted", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    CoroutineScope(Dispatchers.IO).launch {
                        repoViewModel.insert(note)
                    }
                }
        snack.show()
    }


    private fun showUndoDeleteAllNotes(restorePoint: List<Note>) {
        val snack = Snackbar.make(notes_layout, "deleted all notes", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    for (note: Note in restorePoint) {
                        CoroutineScope(Dispatchers.IO).launch {
                            repoViewModel.insert(note)
                        }
                    }
                }
        snack.show()
    }


}