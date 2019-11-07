package bez.dev.featurenotes.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.misc.DInjector
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.view_models.RepoViewModel
import bez.dev.featurenotes.view_models.RepoViewModelFactory

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var repoViewModel: RepoViewModel
    protected lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doMutual()

    }

    private fun doMutual() {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        notificationManager = NotificationManager(this)
        repoViewModel = ViewModelProvider(this, RepoViewModelFactory(DInjector.getRepository())).get(RepoViewModel::class.java)
    }


    protected fun deleteNote(note: Note) {
        notificationManager.cancelNotificationById(note.id)
        repoViewModel.delete(note)
    }


    companion object {
        const val EXTRA_NOTE = "EXTRA_NOTE"
    }


    fun View.toggleShowView(show: Boolean) {
        visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }


}