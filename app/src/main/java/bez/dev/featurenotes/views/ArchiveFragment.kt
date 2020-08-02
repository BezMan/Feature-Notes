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
import kotlinx.android.synthetic.main.fragment_archive.*
import kotlinx.android.synthetic.main.no_notes_layout.*

class ArchiveFragment : Fragment(), ArchiveListAdapter.OnItemClickListener {

    private lateinit var archiveListAdapter: ArchiveListAdapter
    private var archivedList: List<Note> = ArrayList()
    private lateinit var restorePoint: List<Note>
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
        //RECYCLER
        recycler_view_archive.layoutManager = LinearLayoutManager(context)
        recycler_view_archive.setHasFixedSize(true)
        archiveListAdapter = ArchiveListAdapter(this)
        recycler_view_archive.adapter = archiveListAdapter
    }


    private fun initNoteViewModel() {
        baseActivity.repoViewModel.allNotes.observe(viewLifecycleOwner, observer)
    }


    private fun refreshUI() {
        archiveListAdapter.submitList(archivedList)  //reads the adapter DIFF we created, and displays list

        //no notes layout
        no_notes_view.toggleShowView(archivedList.isNullOrEmpty())

        //Notification
        baseActivity.notificationManager.updateNotification(archivedList)
    }


    override fun onNoteItemTextClick(note: Note) {
        baseActivity.editNote(note)
    }

    override fun onNoteItemOverflowClick(note: Note, overflow: ImageView, noteHolder: ArchiveListAdapter.NoteHolder) {
        val popup = PopupMenu(mContext, overflow)
        //Inflating the Popup using xml file
        popup.menuInflater.inflate(R.menu.overflow_note_popup, popup.menu)

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.main_overflow_note_edit -> {
                    baseActivity.editNote(note)
                }
                R.id.main_overflow_note_share -> {
                    baseActivity.shareNote(note)
                }
//                R.id.main_overflow_note_delete -> {
//                    baseActivity.deleteNote(note)
//                    showUndoDelete(note)
//                }
//                R.id.main_overflow_note_archive -> {
//                    baseActivity.archiveNote(note)
//                    showUndoArchive(note)
//                }

            }
            false
        }

        popup.show() //showing popup menu
    }




    companion object {

        fun newInstance() = ArchiveFragment().apply {
//            arguments = Bundle().apply {
//                putInt(FRAGMENT_DATA, filterType) }
        }

    }


}