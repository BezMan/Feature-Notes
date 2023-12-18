package bez.dev.featurenotes.views.screens.archive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.databinding.FragmentArchiveBinding
import bez.dev.featurenotes.databinding.NoNotesLayoutBinding
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.views.presenters.RepoViewModel
import bez.dev.featurenotes.views.screens.ActivityDelegate
import bez.dev.featurenotes.views.screens.ActivityDelegateImpl
import bez.dev.featurenotes.views.screens.ActivityDelegateImpl.Companion.toggleShowView
import bez.dev.featurenotes.views.screens.notes_list.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ArchiveFragment : ActivityDelegate by ActivityDelegateImpl(), Fragment(), ArchiveListAdapter.OnItemClickListener {

    @Inject
    lateinit var notificationManager: NotificationManager

    val baseCoroutineIO = CoroutineScope(Dispatchers.IO)

    private var _bindingNoNotes: NoNotesLayoutBinding? = null
    private val bindingNoNotes get() = _bindingNoNotes!!

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!

    private lateinit var archiveListAdapter: ArchiveListAdapter
    private var archivedList: List<Note> = ArrayList()

    internal val repoViewModel: RepoViewModel by activityViewModels()

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
        _bindingNoNotes = NoNotesLayoutBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        bindingNoNotes.noNotesView.text = resources.getText(R.string.empty_archive)
    }


    private fun initNoteViewModel() {
        repoViewModel.getArchivedNotes().observe(viewLifecycleOwner) {
            archivedList = it
            refreshUI()
        }
    }


    private fun refreshUI() {
        archiveListAdapter.submitList(archivedList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        bindingNoNotes.noNotesView.toggleShowView(archivedList.isEmpty())
    }


    override fun onNoteItemTextClick(note: Note) {
        editNote(requireContext(), note, true)
    }

    override fun onNoteItemUnArchive(note: Note) {
        showUndoArchiveRestore(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView) {
        val popupMenu = PopupMenu(requireContext(), overflow)
        popupMenu.inflate(R.menu.overflow_note_popup_archived)

        addIconsToMenu(popupMenu)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    editNote(requireContext(), note, true) // add param to diff the archived state
                }
                R.id.main_overflow_note_share -> {
                    shareNote(note)
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
        deleteNote(repoViewModel, notificationManager, note)
        val snack = Snackbar.make(binding.notesLayout, note.title + " - note deleted", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
            .setAction("UNDO") {
                // execute when UNDO is clicked
                baseCoroutineIO.launch {
                    repoViewModel.insert(note)
                }
            }
        snack.show()
    }


    private fun showUndoArchiveRestore(note: Note) {
        unArchiveNote(repoViewModel, note)
        val snack = Snackbar.make(binding.notesLayout, note.title + " - note unarchived", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
            .setAction("UNDO") {
                // execute when UNDO is clicked
                baseCoroutineIO.launch {
                    repoViewModel.archive(note)
                }
            }
        snack.show()
    }


}