package bez.dev.featurenotes.services

import android.app.IntentService
import android.content.Intent
import androidx.core.app.RemoteInput
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.data.NoteItem
import bez.dev.featurenotes.misc.DInjector
import bez.dev.featurenotes.misc.NotificationManager
import bez.dev.featurenotes.misc.Utils
import org.koin.android.ext.android.get

class AddFromNotificationIntentService : IntentService("AddFromNotificationIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent?.action == ACTION_REPLY) {
            val note = intent.getParcelableExtra(NOTIFICATION_NOTE) as Note
            val message = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(EXTRA_REPLY)

            handleActionReply(note, message)
        }
    }

    /**
     * Handles action for replying to messages from the notification.
     */
    private fun handleActionReply(note: Note, replyCharSequence: CharSequence?) {

        if (!replyCharSequence.isNullOrBlank()) {
            note.items.add(0, NoteItem(replyCharSequence.toString().trim()) )
        }
        DInjector.getNotes().update(note)
        if (!Utils.isAppForeground) {
            get<NotificationManager>().updateSpecificNotification(note)
        }
    }


    companion object {
        const val ACTION_REPLY = "ACTION_REPLY"
        const val EXTRA_REPLY = "EXTRA_REPLY"
        const val NOTIFICATION_NOTE = "NOTIFICATION_NOTE"
    }


}
