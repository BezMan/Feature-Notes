package bez.dev.featurenotes.views

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.views.BaseActivity.Companion.toggleShowView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_archive.*
import kotlinx.android.synthetic.main.main_activity_toolbar.*
import kotlinx.android.synthetic.main.no_notes_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArchiveFragment : Fragment(), ArchiveListAdapter.OnItemClickListener {

    private lateinit var archiveListAdapter: ArchiveListAdapter
    private var archivedList: List<Note> = ArrayList()
    private lateinit var mContext: Context
    private lateinit var baseActivity: BaseActivity

    private val observer = Observer<List<Note>> {
        archivedList = it
        refreshUI()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_archive, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity = activity as BaseActivity

        initNoteViewModel()

        initUI()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.archive_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initUI() {
        //TOOLBAR
        activity?.toolbar_main_text?.text = resources.getText(R.string.nav_archive)

        //RECYCLER
        recycler_view_archive.layoutManager = LinearLayoutManager(context)
        recycler_view_archive.setHasFixedSize(true)
        archiveListAdapter = ArchiveListAdapter(this)
        recycler_view_archive.adapter = archiveListAdapter

        //TEXT WHEN EMPTY LIST
        no_notes_view.text = resources.getText(R.string.empty_archive)
    }


    private fun initNoteViewModel() {
        baseActivity.repoViewModel.getArchivedNotes().observe(viewLifecycleOwner, observer)
    }


    private fun refreshUI() {
        archiveListAdapter.submitList(archivedList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        no_notes_view.toggleShowView(archivedList.isNullOrEmpty())
    }


    override fun onNoteItemTextClick(note: Note) {
        baseActivity.editNote(note, true)
    }

    override fun onNoteItemUnArchive(note: Note) {
        showUndoArchiveRestore(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: ArchiveListAdapter.NoteHolder) {
        val popupMenu = PopupMenu(mContext, overflow)
        popupMenu.inflate(R.menu.overflow_note_popup_archived)

        baseActivity.addIconsToMenu(popupMenu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    baseActivity.editNote(note, true) // add param to diff the archived state
                }
                R.id.main_overflow_note_share -> {
                    baseActivity.shareNote(note)
                }
                R.id.main_overflow_note_delete -> {
                    showUndoDelete(note)
                }
                R.id.main_overflow_note_unarchive -> {
                    showUndoArchiveRestore(note)
                }

            }
            false
        }

        popupMenu.show() //showing popup menu
    }


    private fun showUndoDelete(note: Note) {
        baseActivity.deleteNote(note)
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


    private fun showUndoArchiveRestore(note: Note) {
        baseActivity.unArchiveNote(note)
        val snack = Snackbar.make(notes_layout, note.title + " - note unarchived", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    CoroutineScope(Dispatchers.IO).launch {
                        baseActivity.repoViewModel.archive(note)
                    }
                }
        snack.show()
    }


    companion object {

        fun newInstance() = ArchiveFragment().apply {
//            arguments = Bundle().apply {
//                putInt(FRAGMENT_DATA, filterType) }
        }

    }


}