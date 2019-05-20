package bez.dev.featurenotes.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bez.dev.featurenotes.R.*
import bez.dev.featurenotes.data.Converters
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.misc.App
import bez.dev.featurenotes.views.DetailPriorityDialog.OnPrioritySaveClickListener
import kotlinx.android.synthetic.main.detail_activity.*
import kotlinx.android.synthetic.main.detail_activity_toolbar.*
import java.text.SimpleDateFormat
import java.util.*


class DetailActivity : BaseActivity(), OnPrioritySaveClickListener, DetailEditTextDialog.OnItemSaveClickListener, DetailListAdapter.OnDetailItemClickListener {

    private var isEditMode: Boolean = false
    private var menuEditItem: MenuItem? = null
    private var menuPriorityItem: MenuItem? = null
    private var mPriority: Int = 0
    private lateinit var detailListAdapter: DetailListAdapter
    private var editTextDialog: DetailEditTextDialog? = null
    private var itemList: MutableList<String> = mutableListOf()
    private lateinit var currentNote: Note
    private var isExistingNote: Boolean = false
    private lateinit var touchHelper: ItemTouchHelper

    private val observer = Observer<String> {
        itemList = Converters.jsonToList(it)
        refreshRecyclerView(itemList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.detail_activity)

        initUI()

        fillExistingData()

    }

    override fun onPause() {
        super.onPause()
        if (editTextDialog != null && editTextDialog!!.isShowing) {
            editTextDialog?.saveMe()

        }
        saveNote()
    }

    override fun onStop() {
        super.onStop()
        editTextDialog?.dismiss()
    }

    private fun initNoteViewModel() {
        repoViewModel.getNoteItems(currentNote).observe(this, observer)
    }

    override fun onDetailItemClick(text: String, position: Int) {
        openEditTextDialog(position, text)
    }

    override fun onDeleteItemClick(position: Int) {
        removeItemAtPosition(position)
        refreshRecyclerView(itemList)
        detailListAdapter.notifyItemRemoved(position)
    }


    override fun onTextSaveDialogBtnClick(newText: String, position: Int, isAddedItem: Boolean) {
        if (isAddedItem) { //NEW
            itemList.add(position, newText)
            refreshRecyclerView(itemList)
            detailListAdapter.notifyItemInserted(position)

            //scroll to show bottom , if we added to bottom
            if (position > 1) {
                nested_scroll_view.post { nested_scroll_view.fullScroll(ScrollView.FOCUS_DOWN) }
            }
        } else { // EDIT
            itemList[position] = newText
            refreshRecyclerView(itemList)
            detailListAdapter.notifyItemChanged(position)

        }
        //remove item if empty
        if (newText.isBlank()) {
            removeItemAtPosition(position)
        }

    }

    private fun removeItemAtPosition(position: Int) {
        itemList.removeAt(position)
    }

    private fun openEditTextDialog(position: Int = 0, text: String = "") {
        editTextDialog = DetailEditTextDialog(this, this, text, position)
        editTextDialog?.show()
    }


    private fun initUI() {
        setSupportActionBar(note_detail_toolbar)    //merges the custom TOOLBAR with the existing MENU

        top_add_item_btn.setOnClickListener { openEditTextDialog(0) }
        bottom_add_item_btn.setOnClickListener { openEditTextDialog(itemList.size) }

        recycler_view_detail.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recycler_view_detail.layoutManager = LinearLayoutManager(this)

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
                Collections.swap(itemList, fromPosition, toPosition)
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
        touchHelper.attachToRecyclerView(recycler_view_detail)
    }


    private fun refreshRecyclerView(notes: List<String>?) {
        detailListAdapter = DetailListAdapter(this, touchHelper, isEditMode)
        recycler_view_detail.adapter = detailListAdapter
        detailListAdapter.submitList(notes)

        if (!isNoteEmpty() && isExistingNote && currentNote.isNotification) {
            currentNote.items = notes.toString()
            App.notificationManager.updateSpecificNotification(currentNote)
        }

    }

    private fun fillExistingData() {
        isExistingNote = intent.hasExtra(EXTRA_NOTE)
        if (isExistingNote) { //EXISTING

            currentNote = intent.getParcelableExtra(EXTRA_NOTE) as Note

            val itemsStr = currentNote.items
            itemList = Converters.jsonToList(itemsStr)

            edit_text_title.setText(currentNote.title)
            mPriority = currentNote.priority

            initNoteViewModel()

        } else { //NEW
            enterEditMode()
        }
    }


    override fun onBackPressed() {
        if (isEditMode) {
            exitEditMode()
        } else {
            super.onBackPressed()
        }
    }


    private fun saveNote() {

        if (isNoteEmpty()) {
            if (isExistingNote) {
                deleteNote(currentNote)
            }
        } else {

            var title = getNoteTitle().toString()
            val priority = mPriority
            val listItemsStr = Converters.listToJson(itemList)

            if (title.isBlank()) { //only title is empty
                title = getTimeStamp()
            }
            if (isExistingNote) {
                currentNote.title = title
                currentNote.priority = priority
                currentNote.items = listItemsStr
                repoViewModel.update(currentNote)
            } else {
                currentNote = Note(title, priority, listItemsStr)
                currentNote.id = repoViewModel.insert(currentNote)

                isExistingNote = true
            }

        }

    }

    private fun isNoteEmpty() = getNoteTitle().isBlank() && itemList.isNullOrEmpty()

    private fun getNoteTitle(): CharSequence {
        return edit_text_title.text
    }

    private fun getTimeStamp(): String {
        val formatter = SimpleDateFormat("h:mm a, d MMM", Locale.getDefault())
        return formatter.format(Date(System.currentTimeMillis()))
    }


    private fun enterEditMode() {
        isEditMode = true
        edit_text_title.setBackgroundColor(ContextCompat.getColor(baseContext, color.white))
        edit_text_title.isEnabled = true
        edit_text_title.isFocusableInTouchMode = true
        edit_text_title.isFocusable = true

        top_add_item_btn.visibility = View.VISIBLE
        bottom_add_item_btn.visibility = View.VISIBLE

        menuEditItem?.setIcon(drawable.ic_close)
        menuPriorityItem?.title = mPriority.toString()

        refreshRecyclerView(itemList)
    }


    private fun exitEditMode() {
        isEditMode = false
        edit_text_title.setBackgroundColor(ContextCompat.getColor(baseContext, android.R.color.transparent))
        edit_text_title.isEnabled = false
        edit_text_title.isFocusableInTouchMode = false
        edit_text_title.isFocusable = false

        top_add_item_btn.visibility = View.GONE
        bottom_add_item_btn.visibility = View.GONE

        menuEditItem?.setIcon(drawable.ic_edit_24dp)
        mPriority = Integer.parseInt(menuPriorityItem?.title.toString())
        menuPriorityItem?.title = ""

        refreshRecyclerView(itemList)
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuEditItem = menu.findItem(id.edit_note)
        menuPriorityItem = menu.findItem(id.edit_priority)

        if (!isExistingNote) { //NEW
            menuEditItem?.setIcon(drawable.ic_close)
            mPriority = resources.getInteger(integer.default_priority)
            menuPriorityItem?.title = mPriority.toString()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(bez.dev.featurenotes.R.menu.detail_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == id.edit_note -> {
                if (!isEditMode) {
                    enterEditMode()
                } else {
                    exitEditMode()
                }
                true
            }
            item.itemId == id.edit_priority -> {
                val priority = Integer.parseInt(item.title.toString())

                val priorityDialog = DetailPriorityDialog(this, priority)
                priorityDialog.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onPrioritySaveBtnClick(newPriority: Int) {
        val priorityStr = newPriority.toString()
        menuPriorityItem?.title = priorityStr
        mPriority = Integer.parseInt(priorityStr)
    }


}
