package bez.dev.featurenotes.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.databinding.FragmentArchiveBinding
import bez.dev.featurenotes.views.BaseActivity.Companion.toggleShowView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class ArchiveFragment : Fragment(), ArchiveListAdapter.OnItemClickListener {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var archiveListAdapter: ArchiveListAdapter
    private var archivedList: List<Note> = ArrayList()
    private lateinit var baseActivity: BaseActivity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity = requireActivity() as BaseActivity

        initNoteViewModel()

        initUI()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.archive_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun initUI() {
        //TOOLBAR
        (requireActivity() as MainActivity).setToolbarText(resources.getText(R.string.nav_archive))

        //RECYCLER
        binding.recyclerViewArchive.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewArchive.setHasFixedSize(true)
        archiveListAdapter = ArchiveListAdapter(this)
        binding.recyclerViewArchive.adapter = archiveListAdapter

        //TEXT WHEN EMPTY LIST
        binding.noNotesLayout.noNotesView.text = resources.getText(R.string.empty_archive)
    }


    private fun initNoteViewModel() {
        baseActivity.repoViewModel.getArchivedNotes().observe(viewLifecycleOwner) {
            archivedList = it
            refreshUI()
        }
    }


    private fun refreshUI() {
        archiveListAdapter.submitList(archivedList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        binding.noNotesLayout.noNotesView.toggleShowView(archivedList.isEmpty())
    }


    override fun onNoteItemTextClick(note: Note) {
        baseActivity.editNote(note, true)
    }

    override fun onNoteItemUnArchive(note: Note) {
        showUndoArchiveRestore(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView) {
        val popupMenu = PopupMenu(baseActivity, overflow)
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
        val snack = Snackbar.make(binding.notesLayout, note.title + " - note deleted", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    baseActivity.baseCoroutineIO.launch {
                        baseActivity.repoViewModel.insert(note)
                    }
                }
        snack.show()
    }


    private fun showUndoArchiveRestore(note: Note) {
        baseActivity.unArchiveNote(note)
        val snack = Snackbar.make(binding.notesLayout, note.title + " - note unarchived", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    baseActivity.baseCoroutineIO.launch {
                        baseActivity.repoViewModel.archive(note)
                    }
                }
        snack.show()
    }


//    companion object {
//
//        fun newInstance() = ArchiveFragment().apply {
//            arguments = Bundle().apply {
//                putInt(FRAGMENT_DATA, filterType) }
//        }
//
//    }


}