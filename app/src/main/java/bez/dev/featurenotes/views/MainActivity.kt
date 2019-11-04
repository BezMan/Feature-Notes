package bez.dev.featurenotes.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_activity_toolbar.*
import kotlinx.android.synthetic.main.no_notes_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity(), MainListAdapter.OnItemClickListener {

    private lateinit var mainListAdapter: MainListAdapter
    private var noteList: List<Note> = ArrayList()

    private val observer = Observer<List<Note>> {
        noteList = it
        this.refreshUI(noteList)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        initUI()

        initNoteViewModel()

    }

    private fun initUI() {
        //TOOLBAR
        setSupportActionBar(main_list_toolbar)    //merges the custom TOOLBAR with the existing MENU

        //RECYCLER
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
    }


    private fun showUndoDelete(note: Note) {
        val snack = Snackbar.make(main_layout, note.title + " - note deleted", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    CoroutineScope(Dispatchers.Default).launch {
                        repoViewModel.insert(note)
                    }
                }
        snack.show()
    }


    private fun initNoteViewModel() {
        repoViewModel.allNotes.observe(this, observer)
    }


    private fun refreshUI(notes: List<Note>?) {
        //RecyclerView
        mainListAdapter = MainListAdapter(this)
        recycler_view.adapter = mainListAdapter
        mainListAdapter.submitList(notes)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        no_notes_view.toggleShowView(notes.isNullOrEmpty())

        //Notification
        notificationManager.updateNotification(noteList)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.main_menu_add_note -> {
                addNote()
                true
            }
            R.id.main_menu_reset_all_notifications -> {
                resetAllNotifications()
                true
            }
            R.id.main_menu_delete_all_notes -> {
                deleteNotes()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetAllNotifications() {
        repoViewModel.resetAllNotifications()
    }

    override fun onNoteItemTextClick(note: Note) {
        editNote(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: MainListAdapter.NoteHolder) {
        val popup = PopupMenu(this, overflow)
        //Inflating the Popup using xml file
        popup.menuInflater.inflate(R.menu.overflow_note_popup, popup.menu)

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    editNote(note)
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

    override fun onToggleNotificationClick(note: Note, isChecked: Boolean) {
        if (isChecked != note.isNotification) {
            notificationManager.cancelNotificationById(note.id)

            note.isNotification = isChecked
            repoViewModel.update(note)
        }

    }


    private fun addNote() {
        val intent = Intent(this, DetailActivity::class.java)
        startActivity(intent)
    }

    private fun editNote(note: Note) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        startActivity(intent)
    }

    private fun deleteNotes() {
        if (!noteList.isNullOrEmpty()) {
            Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show()
            repoViewModel.deleteAllNotes()
        }
    }


}
