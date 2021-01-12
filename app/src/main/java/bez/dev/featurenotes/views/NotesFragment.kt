package bez.dev.featurenotes.views

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
import bez.dev.featurenotes.views.BaseActivity.Companion.toggleShowView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notes.*
import kotlinx.android.synthetic.main.main_activity_toolbar.*
import kotlinx.android.synthetic.main.no_notes_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesFragment : Fragment(), MainListAdapter.OnItemClickListener {

    private lateinit var mainListAdapter: MainListAdapter
    private var noteList: List<Note> = ArrayList()
    private lateinit var restorePoint: List<Note>
    private lateinit var baseActivity: BaseActivity


    private val observer = Observer<List<Note>> {
        noteList = it
        refreshUI()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity = activity as BaseActivity

        initNoteViewModel()

        initUI()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notes_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initUI() {
        //TOOLBAR
        activity?.toolbar_main_text?.text = resources.getText(R.string.app_name)

        //RECYCLER
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
        mainListAdapter = MainListAdapter(this)
        recycler_view.adapter = mainListAdapter

        //TEXT WHEN EMPTY LIST
        no_notes_view.text = resources.getText(R.string.empty_notes)

        //FAB
        fab_add_note.setOnClickListener { baseActivity.addNote() }
    }


    private fun initNoteViewModel() {
        baseActivity.repoViewModel.getAllNotes().observe(viewLifecycleOwner, observer)
    }


    private fun refreshUI() {
        mainListAdapter.submitList(noteList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        no_notes_view.toggleShowView(noteList.isNullOrEmpty())

        //Notification
        baseActivity.notificationManager.updateNotification(noteList)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_add_note -> {
                baseActivity.addNote()
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
            baseActivity.repoViewModel.resetAllNotifications()
        }
    }


    override fun onNoteItemTextClick(note: Note) {
        baseActivity.editNote(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: MainListAdapter.NoteHolder) {
        val popupMenu = PopupMenu(baseActivity, overflow)
        popupMenu.inflate(R.menu.overflow_note_popup)

        baseActivity.addIconsToMenu(popupMenu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    baseActivity.editNote(note)
                }
                R.id.main_overflow_note_share -> {
                    baseActivity.shareNote(note)
                }
                R.id.main_overflow_note_delete -> {
                    baseActivity.deleteNote(note)
                    showUndoDelete(note)
                }
                R.id.main_overflow_note_archive -> {
                    baseActivity.archiveNote(note)
                    showUndoArchive(note)
                }
                R.id.main_overflow_note_notification -> {
                    noteHolder.checkboxToggleNotification.performClick()
                }

            }
            false
        }

        popupMenu.show() //showing popup menu
    }




    override fun onToggleNotificationClick(note: Note, isChecked: Boolean) {
        if (isChecked != note.isNotification) {
            baseActivity.notificationManager.cancelNotificationById(note.id)

            note.isNotification = isChecked
            baseActivity.repoViewModel.update(note)
        }

    }


    private fun checkDeleteAllNotes() {
        if (noteList.isNotEmpty()) {
            restorePoint = noteList

            val builder = AlertDialog.Builder(baseActivity)
            builder.setMessage("All notes will be deleted.. \n Are you sure?")
                    .setPositiveButton("Yes") { dialog, id ->
                        baseActivity.repoViewModel.deleteAllNotes()
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
                        baseActivity.repoViewModel.insert(note)
                    }
                }
        snack.show()
    }


    private fun showUndoArchive(note: Note) {
        val snack = Snackbar.make(notes_layout, note.title + " - note archived", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    CoroutineScope(Dispatchers.IO).launch {
                        baseActivity.repoViewModel.unArchive(note)
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
                            baseActivity.repoViewModel.insert(note)
                        }
                    }
                }
        snack.show()
    }


//    companion object {
//
//        fun newInstance() = NotesFragment().apply {
//            arguments = Bundle().apply {
//                putInt(FRAGMENT_DATA, filterType) }
//        }
//
//    }


}