package bez.dev.featurenotes.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.RemoteInput
import bez.dev.featurenotes.data.domain.IRepository
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.data.domain.NoteItem
import bez.dev.featurenotes.misc.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var noteRepository: IRepository


    override fun onReceive(context: Context, intent: Intent) {
        val note = intent.let {
            it.getParcelableExtra(NOTIFICATION_NOTE) as Note?
        }

        note?.let {
            if (intent.action == ACTION_REPLY) {
                handleActionAdd(note, intent)
            } else if (intent.action == ACTION_DISMISS) {
                handleActionDismiss(note)
            }
        }

    }

    /**
     * Handles notification action for DISMISS .
     */
    private fun handleActionDismiss(note: Note) {
        note.isNotification = false

        noteRepository.update(note)
        notificationManager.cancelNotificationById(note.id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.getNotificationCount() == 1) { //we just decreased to 0, so remove summary notification
                notificationManager.cancelNotifications()
            }
        }
    }

    /**
     * Handles notification action for ADD items.
     */
    private fun handleActionAdd(note: Note, intent: Intent) {
        val message = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(
            EXTRA_REPLY
        )
        if (!message.isNullOrBlank()) {
            note.items.add(0, NoteItem(message.toString().trim()))
        }
        noteRepository.update(note)
        notificationManager.updateSpecificNotification(note)
    }

    companion object {
        const val ACTION_REPLY = "ACTION_REPLY"
        const val ACTION_DISMISS = "ACTION_DISMISS"
        const val EXTRA_REPLY = "EXTRA_REPLY"
        const val NOTIFICATION_NOTE = "NOTIFICATION_NOTE"
    }

}
