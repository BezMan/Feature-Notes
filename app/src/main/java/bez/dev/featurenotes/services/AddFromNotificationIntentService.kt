package bez.dev.featurenotes.services

import android.app.IntentService
import android.content.Intent
import androidx.core.app.RemoteInput
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.data.NoteItem
import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.misc.NotificationManager
import org.koin.android.ext.android.get

class AddFromNotificationIntentService : IntentService("AddFromNotificationIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val note = intent?.getParcelableExtra(NOTIFICATION_NOTE) as Note

        if (intent.action == ACTION_REPLY) {
            handleActionReply(note, intent)
        }
        else if (intent.action == ACTION_DISMISS){
            handleActionDismiss(note)
        }
    }

    /**
     * Handles notification action for DISMISS .
     */
    private fun handleActionDismiss(note: Note) {
        note.isNotification = false

        get<NoteRepository>().update(note)
//        if (!Utils.isAppForeground) {
            get<NotificationManager>().cancelNotificationById(note.id)
//        }
    }

    /**
     * Handles notification action for ADD items.
     */
    private fun handleActionReply(note: Note, intent: Intent?) {
        val message = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(EXTRA_REPLY)
        if (!message.isNullOrBlank()) {
            note.items.add(0, NoteItem(message.toString().trim()) )
        }
        get<NoteRepository>().update(note)
//        if (!Utils.isAppForeground) {
            get<NotificationManager>().updateSpecificNotification(note)
//        }
    }


    companion object {
        const val ACTION_REPLY = "ACTION_REPLY"
        const val ACTION_DISMISS = "ACTION_DISMISS"
        const val EXTRA_REPLY = "EXTRA_REPLY"
        const val NOTIFICATION_NOTE = "NOTIFICATION_NOTE"
    }


}
