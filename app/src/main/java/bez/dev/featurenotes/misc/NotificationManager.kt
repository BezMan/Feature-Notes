package bez.dev.featurenotes.misc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.services.AddFromNotificationIntentService
import bez.dev.featurenotes.views.MainActivity

class NotificationManager(context: Context) {

    private val mContext = context
    private val mNotificationManager: NotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var mNotificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)

    private val mainPendingIntent = PendingIntent.getActivity(mContext,
            0, Intent(mContext, MainActivity::class.java), 0)

    init {

        // NotificationChannel is required on Oreo and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(NotificationChannel(
                    CHANNEL_ID,
                    mContext.getString(bez.dev.featurenotes.R.string.channel_name),
                    NotificationManager.IMPORTANCE_LOW))
        }

        mNotificationBuilder
                .setSmallIcon(bez.dev.featurenotes.R.mipmap.ic_app_launcher)
                .setColor(ContextCompat.getColor(context, bez.dev.featurenotes.R.color.colorPrimary))
                .setGroup(KEY_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setContentIntent(mainPendingIntent)

    }

    private fun setSummaryBuilder(): Notification {
        return mNotificationBuilder.build()
    }


    fun cancelNotifications() {
        mNotificationManager.cancelAll()
    }

    fun cancelNotificationById(noteId: Long) {
        mNotificationManager.cancel(noteId.toInt())
    }


    fun updateSpecificNotification(note: Note) {
        NotificationManagerCompat.from(mContext).apply {
            notify(note.id.toInt(), createNotificationBuilder(note))
        }
    }

    fun updateNotification(notes: List<Note>) {
        var count = 0

        for (i in notes.indices) {
            val note = notes[i]

            if (note.isNotification) {
                ++count
                updateSpecificNotification(note)
            }
        }
        if (count > 0) {
            NotificationManagerCompat.from(mContext).apply {
                notify(NOTIFICATION_ID, setSummaryBuilder())
            }
        } else {
            cancelNotifications()   //mainly for summary
        }
    }


    private fun createNotificationBuilder(note: Note): Notification {

        // Create the RemoteInput specifying this key.
        val replyLabel = "ADD"
        val remoteInput = RemoteInput.Builder(AddFromNotificationIntentService.EXTRA_REPLY)
                .setLabel(replyLabel)
                .build()

        // Pending intent =
        //      API <24 (M and below): activity so the lock-screen presents the auth challenge.
        //      API 24+ (N and above): this should be a Service or BroadcastReceiver.
        val replyActionPendingIntent: PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val intent = Intent(mContext, AddFromNotificationIntentService::class.java)
            intent.action = AddFromNotificationIntentService.ACTION_REPLY
            intent.putExtra(AddFromNotificationIntentService.NOTIFICATION_NOTE, note)
            replyActionPendingIntent = PendingIntent.getService(mContext, note.id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        } else {
            replyActionPendingIntent = mainPendingIntent
        }

        val replyAction = NotificationCompat.Action.Builder(
                bez.dev.featurenotes.R.drawable.ic_add,
                replyLabel,
                replyActionPendingIntent)
                .addRemoteInput(remoteInput)
                // Informs system we aren't bringing up our own custom UI for a reply
                .setShowsUserInterface(false)
                .build()


        val inboxStyle = NotificationCompat.InboxStyle()
//        inboxStyle.setSummaryText("best notes app :) ")
//        inboxStyle.setBigContentTitle("notes")

        val list = note.items

        for (i in 0 until list.size) {
            val itemText = list[i].itemText
            if (list[i].isDone){
                inboxStyle.addLine(strikeThroughText(itemText))
            }else{
                inboxStyle.addLine(itemText)
            }
        }

        return setBodyNotification(note, replyAction, inboxStyle)
    }

    private fun setBodyNotification(note: Note, replyAction: NotificationCompat.Action?, inboxStyle: NotificationCompat.InboxStyle): Notification {
        return NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(bez.dev.featurenotes.R.mipmap.ic_app_launcher)
                .setContentTitle(boldText(note.title))
                .setGroup(KEY_NOTIFICATION_GROUP)
                .setOngoing(true)
                .setContentIntent(mainPendingIntent)
                .addAction(replyAction)
                .setStyle(inboxStyle)

                .build()
    }

    private fun boldText(text: String): CharSequence {
        val sp = SpannableString(text)
        sp.setSpan(StyleSpan(Typeface.BOLD), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }

    private fun strikeThroughText(text: String): CharSequence {
        val sp = SpannableString(text)
        sp.setSpan(StrikethroughSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }


    companion object {
        private const val KEY_NOTIFICATION_GROUP = "KEY_NOTIFICATION_GROUP"
        private const val CHANNEL_ID = "notes"
        private const val NOTIFICATION_ID = -1000
    }

}
