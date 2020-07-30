package bez.dev.featurenotes.views

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.view_models.RepoViewModel
import org.koin.android.ext.android.get

abstract class BaseActivity : AppCompatActivity() {

    protected var repoViewModel = get<RepoViewModel>()
    val notificationManager = get<NotificationManager>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doMutual()

    }

    private fun doMutual() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


    protected fun deleteNote(note: Note) {
        notificationManager.cancelNotificationById(note.id)
        repoViewModel.delete(note)
    }


    protected fun shareNote(note: Note) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, note.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
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