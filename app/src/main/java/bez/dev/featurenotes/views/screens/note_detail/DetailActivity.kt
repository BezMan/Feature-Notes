package bez.dev.featurenotes.views.screens.note_detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.data.domain.NoteItem
import bez.dev.featurenotes.databinding.DetailActivityBinding
import bez.dev.featurenotes.views.screens.BaseActivity
import bez.dev.featurenotes.views.screens.note_detail.DetailPriorityDialog.OnPrioritySaveClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : BaseActivity(), OnPrioritySaveClickListener,
    DetailEditTextDialog.OnItemSaveClickListener, DetailListAdapter.OnDetailItemClickListener {

    private var isEditMode: Boolean = false
    private var menuEditItem: MenuItem? = null
    private var menuPriorityItem: MenuItem? = null
    private var menuShare: MenuItem? = null
    private var menuUnarchive: MenuItem? = null
    private lateinit var detailListAdapter: DetailListAdapter
    private var editTextDialog: DetailEditTextDialog? = null
    private lateinit var currentNote: Note
    private lateinit var revertedNote: Note
    private var isExistingNote: Boolean = false
    private lateinit var touchHelper: ItemTouchHelper

    private lateinit var _binding: DetailActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DetailActivityBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        initUI()

        checkIsExistingNote()

        handleBackPress()

    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    isEditMode ->
                        exitEditMode()
                    isNoteEmpty() ->
                        checkDiscardEmpty()
                    isTitleBlank() ->
                        fillTitleWithTimestamp()
                    else ->
                        finish()
                }
            }
        })

    }

    override fun onPause() {
        super.onPause()
        if (editTextDialog != null && editTextDialog!!.isShowing) {
            editTextDialog?.saveMe()
        }
    }

    override fun onStop() {
        super.onStop()
        editTextDialog?.dismiss()
    }

    private fun observeNote() {
        repoViewModel.getNoteById(currentNote.id).observe(this) {
            currentNote = it
            refreshRecyclerView()
        }
    }


    override fun onDetailItemClickToggleDone(isItemDone: Boolean, position: Int) {
        currentNote.items[position].isDone = isItemDone
        detailListAdapter.notifyItemChanged(position)
        saveNote()
    }

    override fun onDetailItemClick(noteItem: NoteItem, position: Int) {
        openEditTextDialog(position, noteItem)
    }

    override fun onDetailItemLongClick(text: String, position: Int): Boolean {
        copyTextToClipboard(text)
        return true
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("item", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteItemClick(position: Int) {
        val strText = deleteItemAtPosition(position)
        refreshRecyclerView()
        detailListAdapter.notifyItemRemoved(position)
        showUndoDelete(position, strText)
    }

    private fun showUndoDelete(position: Int, strText: String) {
        val snack = Snackbar.make(_binding.detailLayout, "item deleted", Snackbar.LENGTH_INDEFINITE)

        snack.setDuration(8000)
                .setAction("UNDO") {
                    // execute when UNDO is clicked
                    addItemAtPosition(position, strText)
                }
        snack.show()
    }


    override fun onTextSaveDialogBtnClick(noteItem: NoteItem, position: Int, isNewItem: Boolean) {
        if (isNewItem) { //NEW
            addItemAtPosition(position, noteItem.itemText)

            //scroll to show bottom , if we added to bottom
            if (position > 1) {
                _binding.nestedScrollView.post { _binding.nestedScrollView.fullScroll(ScrollView.FOCUS_DOWN) }
            }
        } else { // EDIT
            currentNote.items[position] = noteItem
            refreshRecyclerView()
            detailListAdapter.notifyItemChanged(position)

        }
        //remove item if empty
        if (noteItem.itemText.isBlank()) {
            deleteItemAtPosition(position)
        }

    }

    private fun addItemAtPosition(position: Int, str: String) {
        currentNote.items.add(position, NoteItem(str))
        refreshRecyclerView()
        detailListAdapter.notifyItemInserted(position)
    }

    private fun deleteItemAtPosition(position: Int): String {
        return currentNote.items.removeAt(position).itemText
    }

    private fun openEditTextDialog(position: Int = 0, noteItem: NoteItem) {
        editTextDialog = DetailEditTextDialog(this, noteItem, position)
        editTextDialog?.show()
    }


    private fun initUI() {
        //TOOLBAR
        setSupportActionBar(_binding.detailActivityToolbar.noteDetailToolbar)    //merges the custom TOOLBAR with the existing MENU

        _binding.topAddItemBtn.btnAddItem.setOnClickListener { openEditTextDialog(0, NoteItem("")) }
        _binding.bottomAddItemBtn.btnAddItem.setOnClickListener { openEditTextDialog(currentNote.items.size, NoteItem("")) }

        _binding.recyclerViewDetail.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        _binding.recyclerViewDetail.layoutManager = LinearLayoutManager(this)

        dragItemLogic()
    }

    private fun dragItemLogic() {
        touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            var dragFrom = -1
            var dragTo = -1

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                if (dragFrom == -1) {
                    dragFrom = fromPosition
                }
                dragTo = toPosition
                Collections.swap(currentNote.items, fromPosition, toPosition)
                detailListAdapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            private fun itemDragFinished(/*from: Int, to: Int*/) {
                detailListAdapter.notifyDataSetChanged()
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                    itemDragFinished(/*dragFrom, dragTo*/)
                }
                dragFrom = -1
                dragTo = -1
            }

            override fun isLongPressDragEnabled(): Boolean {
                return false
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

        })
        touchHelper.attachToRecyclerView(_binding.recyclerViewDetail)
    }


    private fun refreshRecyclerView() {
        detailListAdapter = DetailListAdapter(this, touchHelper, isEditMode)
        _binding.recyclerViewDetail.adapter = detailListAdapter
        detailListAdapter.submitList(currentNote.items)

        if (!isNoteEmpty() && isExistingNote && currentNote.isNotification) {
            notificationManager.updateSpecificNotification(currentNote)
        }

    }

    private fun checkIsExistingNote() {
        isExistingNote = intent.hasExtra(EXTRA_NOTE)
        if (isExistingNote) {

            currentNote = intent.getParcelableExtra(EXTRA_NOTE)!!
            revertedNote = Note(currentNote.title, currentNote.priority, currentNote.items)

            _binding.detailActivityToolbar.editTextTitle.setText(currentNote.title)

            observeNote()

        } else { //NEW NOTE
            currentNote = Note()
            revertedNote = Note()
            enterEditMode()
        }
    }

    private fun revertNote() {
        currentNote.items = revertedNote.items
        currentNote.priority = revertedNote.priority
        menuPriorityItem?.title = currentNote.priority.toString()
        _binding.detailActivityToolbar.editTextTitle.setText(revertedNote.title)
        exitEditMode()
    }


    private fun saveNote() {

        val title = getNoteTitle().toString()

        if (isExistingNote) {
            currentNote.title = title
            repoViewModel.update(currentNote)
        } else { // create new note
            currentNote = Note(title, currentNote.priority, currentNote.items)

            baseCoroutineIO.launch {
                currentNote.id = repoViewModel.insert(currentNote)
            }
            isExistingNote = true
        }
    }

    private fun checkDiscardEmpty() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("empty note will be discarded.. \n Are you sure?")
                .setPositiveButton("Discard") { dialog, id ->
                    finish()
                    deleteNote(currentNote)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.dismiss()
                }

        val alert = builder.create()
        alert.show()
    }

    private fun fillTitleWithTimestamp() {
        currentNote.title = getTimeStamp()
        repoViewModel.update(currentNote) // thinking this must happen only after we inserted the new item already
        finish()
    }

    private fun isNoteEmpty() = getNoteTitle().isBlank() && currentNote.items.isEmpty()

    private fun isTitleBlank() = getNoteTitle().isBlank()

    private fun getNoteTitle(): CharSequence {
        return _binding.detailActivityToolbar.editTextTitle.text
    }

    private fun getTimeStamp(): String {
        val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        return formatter.format(Date(System.currentTimeMillis()))
    }


    private fun enterEditMode() {
        isEditMode = true
        _binding.detailActivityToolbar.editTextTitle.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
        _binding.detailActivityToolbar.editTextTitle.isEnabled = true
        _binding.detailActivityToolbar.editTextTitle.isFocusableInTouchMode = true
        _binding.detailActivityToolbar.editTextTitle.isFocusable = true

        _binding.topAddItemBtn.btnAddItem.visibility = View.VISIBLE
        _binding.bottomAddItemBtn.btnAddItem.visibility = View.VISIBLE

        menuEditItem?.setIcon(R.drawable.ic_close)
        menuPriorityItem?.isVisible = true
        menuPriorityItem?.title = currentNote.priority.toString()
        menuShare?.isVisible = false
        refreshRecyclerView()
    }


    private fun exitEditMode() {
        isEditMode = false
        _binding.detailActivityToolbar.editTextTitle.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.transparent))
        _binding.detailActivityToolbar.editTextTitle.isEnabled = false
        _binding.detailActivityToolbar.editTextTitle.isFocusableInTouchMode = false
        _binding.detailActivityToolbar.editTextTitle.isFocusable = false

        _binding.topAddItemBtn.btnAddItem.visibility = View.GONE
        _binding.bottomAddItemBtn.btnAddItem.visibility = View.GONE

        menuEditItem?.setIcon(R.drawable.ic_edit_white)

        val priorityStr = menuPriorityItem?.title.toString()
        if (priorityStr.isNotEmpty()) {
            currentNote.priority = Integer.parseInt(priorityStr)
        }
        menuPriorityItem?.isVisible = false
        menuShare?.isVisible = true

        refreshRecyclerView()
        //saving frequently so we can SHARE most updated note items
        saveNote()
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuEditItem = menu.findItem(R.id.menu_detail_edit_note)
        menuPriorityItem = menu.findItem(R.id.menu_detail_priority)
        menuShare = menu.findItem(R.id.menu_detail_share)
        menuUnarchive = menu.findItem(R.id.menu_detail_unarchive)

        if (!isExistingNote) { //NEW note - init menu icons
            currentNote.priority = getSavedDefaultPriority()

            menuEditItem?.setIcon(R.drawable.ic_close)
            menuPriorityItem?.title = currentNote.priority.toString()
            menuPriorityItem?.isVisible = true
            menuShare?.isVisible = false
        } else {
            menuPriorityItem?.isVisible = false
            menuUnarchive?.isVisible = intent.getBooleanExtra(EXTRA_IS_ARCHIVED, false)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.detail_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_detail_share -> {
                shareNote(currentNote)
            }
            R.id.menu_detail_revert -> {
                revertNote()
            }
            R.id.menu_detail_unarchive -> {
                unArchiveNote(currentNote)
                finish()
            }
            R.id.menu_detail_edit_note -> {
                if (!isEditMode) {
                    enterEditMode()
                } else {
                    exitEditMode()
                }
            }
            R.id.menu_detail_priority -> {
                val priorityDialog = DetailPriorityDialog(this, currentNote.priority)
                priorityDialog.show()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    override fun onPrioritySaveBtnClick(newPriority: Int) {
        currentNote.priority = newPriority
        menuPriorityItem?.title = newPriority.toString()
    }


}
