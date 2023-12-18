package bez.dev.featurenotes.views.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.preference.PreferenceManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.views.presenters.RepoViewModel
import bez.dev.featurenotes.views.screens.note_detail.DetailActivity


interface ActivityDelegate {
    fun addNote(context: Context)
    fun editNote(context: Context, note: Note, isArchived: Boolean = false)
    fun shareNote(note: Note)
    fun addIconsToMenu(popupMenu: PopupMenu)
    fun getSavedDefaultPriority(): Int
    fun archiveNote(repoViewModel: RepoViewModel, note: Note)
    fun unArchiveNote(repoViewModel: RepoViewModel, note: Note)
    fun deleteNote(
        repoViewModel: RepoViewModel,
        notificationManager: NotificationManager,
        note: Note
    )
}


open class ActivityDelegateImpl: ActivityDelegate, AppCompatActivity() {


    override fun addNote(context: Context) {
        val intent = Intent(context, DetailActivity::class.java)
        context.startActivity(intent)
    }

    override fun editNote(context: Context, note: Note, isArchived: Boolean) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        intent.putExtra(EXTRA_IS_ARCHIVED, isArchived)
        context.startActivity(intent)
    }

    override fun deleteNote(repoViewModel: RepoViewModel, notificationManager: NotificationManager, note: Note) {
        notificationManager.cancelNotificationById(note.id)
        repoViewModel.delete(note)
    }


    override fun archiveNote(repoViewModel: RepoViewModel, note: Note) {
        repoViewModel.archive(note)
    }

    override fun unArchiveNote(repoViewModel: RepoViewModel, note: Note) {
        repoViewModel.unArchive(note)
    }

    override fun shareNote(note: Note) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, note.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


    override fun addIconsToMenu(popupMenu: PopupMenu) {
        try {
            val declaredField = PopupMenu::class.java.getDeclaredField("mPopup")
            declaredField.isAccessible = true
            val mPopup = declaredField.get(popupMenu)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("PopupMenu", "Error showing menu icons", e)
        }
    }


    override fun getSavedDefaultPriority(): Int {
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val defPriority = defaultSharedPreferences.getString(resources.getString(R.string.note_preferences), "3")
        return Integer.parseInt(defPriority!!)
    }


    companion object {
        const val FRAGMENT_DATA = "FRAGMENT_DATA"

        const val EXTRA_NOTE = "EXTRA_NOTE"
        const val EXTRA_IS_ARCHIVED = "EXTRA_IS_ARCHIVED"

        fun View.toggleShowView(show: Boolean) {
            visibility = if (show) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

}