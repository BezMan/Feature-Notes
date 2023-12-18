package bez.dev.featurenotes.views.screens.notes_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.databinding.FragmentNotesBinding
import bez.dev.featurenotes.databinding.NoNotesLayoutBinding
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.views.presenters.RepoViewModel
import bez.dev.featurenotes.views.screens.ImageActivity
import bez.dev.featurenotes.views.screens.ActivityDelegate
import bez.dev.featurenotes.views.screens.ActivityDelegateImpl
import bez.dev.featurenotes.views.screens.ActivityDelegateImpl.Companion.toggleShowView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotesFragment : ActivityDelegate by ActivityDelegateImpl(), Fragment(), MainListAdapter.OnItemClickListener {

    @Inject
    lateinit var notificationManager: NotificationManager


    private var _bindingNoNotes: NoNotesLayoutBinding? = null
    private val bindingNoNotes get() = _bindingNoNotes!!

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainListAdapter: MainListAdapter
    private var noteList: List<Note> = ArrayList()
    private lateinit var restorePoint: List<Note>

    val baseCoroutineIO = CoroutineScope(Dispatchers.IO)

    private val repoViewModel: RepoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        _bindingNoNotes = NoNotesLayoutBinding.bind(binding.root)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNoteViewModel()

        initUI()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.notes_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initUI() {
        //TOOLBAR
        (requireActivity() as MainActivity).setToolbarText(resources.getText(R.string.nav_notes))

        //RECYCLER
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        mainListAdapter = MainListAdapter(this)
        binding.recyclerView.adapter = mainListAdapter

        //TEXT WHEN EMPTY LIST
        bindingNoNotes.noNotesView.text = resources.getText(R.string.empty_notes)

        //FAB
        binding.fabAddNote.setOnClickListener { addNote(requireContext()) }
    }


    private fun initNoteViewModel() {
        repoViewModel.getAllNotes().observe(viewLifecycleOwner) {
            noteList = it
            refreshUI()
        }
    }


    private fun refreshUI() {
        mainListAdapter.submitList(noteList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        bindingNoNotes.noNotesView.toggleShowView(noteList.isEmpty())

        //Notification
        notificationManager.updateNotification(noteList)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_add_note -> {
                addNote(requireContext())
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
        editNote(requireContext(), note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: MainListAdapter.NoteHolder) {
        val popupMenu = PopupMenu(requireContext(), overflow)
        popupMenu.inflate(R.menu.overflow_note_popup)

        addIconsToMenu(popupMenu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    editNote(requireContext(), note)
                }
                R.id.main_overflow_note_share -> {
                    shareNote(note)
                }
                R.id.main_overflow_note_delete -> {
                    deleteNote(repoViewModel, notificationManager, note)
                    showUndoDelete(note)
                }
                R.id.main_overflow_note_archive -> {
                    archiveNote(repoViewModel, note)
                    showUndoArchive(note)
                }
                R.id.main_overflow_note_notification -> {
                    noteHolder.toggleCheckBox()
                }

            }
            false
        }

        popupMenu.show() //showing popup menu
    }




    override fun onToggleNotificationClick(note: Note, isChecked: Boolean) {
        if (isChecked != note.isNotification) {
            notificationManager.cancelNotificationById(note.id)

            note.isNotification = isChecked
            repoViewModel.update(note)
        }

    }


    private fun checkDeleteAllNotes() {
        if (noteList.isNotEmpty()) {
            restorePoint = noteList

            val builder = AlertDialog.Builder(requireContext())
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
        val snack = Snackbar.make(bindingNoNotes.noNotesView, note.title + " - note deleted", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    baseCoroutineIO.launch {
                        repoViewModel.insert(note)
                    }
                }
        snack.show()
    }


    private fun showUndoArchive(note: Note) {
        val snack = Snackbar.make(bindingNoNotes.noNotesView, note.title + " - note archived", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    baseCoroutineIO.launch {
                        repoViewModel.unArchive(note)
                    }
                }
        snack.show()
    }



    private fun showUndoDeleteAllNotes(restorePoint: List<Note>) {
        val snack = Snackbar.make(bindingNoNotes.noNotesView, "deleted all notes", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    for (note: Note in restorePoint) {
                        baseCoroutineIO.launch {
                            repoViewModel.insert(note)
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