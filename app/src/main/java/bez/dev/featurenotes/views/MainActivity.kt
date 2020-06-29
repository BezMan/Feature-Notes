package bez.dev.featurenotes.views

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_activity_toolbar.*
import kotlinx.android.synthetic.main.no_notes_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity(), MainListAdapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainListAdapter: MainListAdapter
    private var noteList: List<Note> = ArrayList()
    private lateinit var restorePoint: List<Note>

    private val observer = Observer<List<Note>> {
        noteList = it
        refreshUI()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isCalledFromSummaryNotification()) return

        setContentView(R.layout.main_activity)

        initUI()

        initNoteViewModel()

    }

    /**
     * summary notification click - should RESUME app and not start another MainActivity
     */
    private fun isCalledFromSummaryNotification(): Boolean {
        if (!isTaskRoot
        //                && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return true
        }
        return false
    }

    private fun initUI() {
        //TOOLBAR
        setSupportActionBar(main_list_toolbar)    //merges the custom TOOLBAR with the existing MENU

        //RECYCLER
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        mainListAdapter = MainListAdapter(this)
        recycler_view.adapter = mainListAdapter

        //NAVIGATION DRAWER
        val toggle = ActionBarDrawerToggle(this, drawer_layout, main_list_toolbar, 0, 0)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
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
                val intent = Intent(this, ImageActivity::class.java)
                startActivity(intent)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_notes -> {
                Toast.makeText(this, "nav_notes clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_archive -> {
                Toast.makeText(this, "nav_archive clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_settings -> {
                Toast.makeText(this, "nav_settings clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
        val popup = PopupMenu(this, overflow)
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

    override fun onToggleNotificationClick(note: Note, isChecked: Boolean) {
        if (isChecked != note.isNotification) {
            notificationManager.cancelNotificationById(note.id)

            note.isNotification = isChecked
            repoViewModel.update(note)
        }

    }


    fun addNote(view: View) {
        addNote()
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


    private fun checkDeleteAllNotes() {
        if (noteList.isNotEmpty()) {
            restorePoint = noteList

            val builder = AlertDialog.Builder(this)
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
        val snack = Snackbar.make(main_layout, note.title + " - note deleted", Snackbar.LENGTH_INDEFINITE)

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
        val snack = Snackbar.make(main_layout, "deleted all notes", Snackbar.LENGTH_INDEFINITE)

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


    companion object {
        const val EXTRA_NOTE = "EXTRA_NOTE"

        fun View.toggleShowView(show: Boolean) {
            visibility = if (show) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

}
