package bez.dev.featurenotes.views.screens

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.preference.PreferenceManager
import bez.dev.featurenotes.R
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.views.screens.note_detail.DetailActivity
import bez.dev.featurenotes.views.presenters.RepoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var notificationManager: NotificationManager

    internal val repoViewModel: RepoViewModel by viewModels()

    val baseCoroutineIO = CoroutineScope(Dispatchers.IO)

    fun deleteNote(note: Note) {
        notificationManager.cancelNotificationById(note.id)
        repoViewModel.delete(note)
    }


    fun archiveNote(note: Note) {
        repoViewModel.archive(note)
    }

    fun unArchiveNote(note: Note) {
        repoViewModel.unArchive(note)
    }


    fun addNote() {
        val intent = Intent(this, DetailActivity::class.java)
        startActivity(intent)
    }

    fun editNote(note: Note, isArchived: Boolean = false) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_NOTE, note)
        intent.putExtra(EXTRA_IS_ARCHIVED, isArchived)
        startActivity(intent)
    }

    fun shareNote(note: Note) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, note.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }


    fun addIconsToMenu(popupMenu: PopupMenu) {
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


    fun getSavedDefaultPriority(): Int {
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