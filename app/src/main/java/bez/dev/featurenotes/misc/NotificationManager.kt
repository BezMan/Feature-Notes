package bez.dev.featurenotes.misc

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.services.AddFromNotificationIntentService
import bez.dev.featurenotes.views.screens.BaseActivity.Companion.EXTRA_NOTE
import bez.dev.featurenotes.views.screens.note_detail.DetailActivity
import bez.dev.featurenotes.views.screens.notes_list.MainActivity
import kotlin.random.Random

class NotificationManager (val context: Context) {

    private val mNotificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var mNotificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)

    private val mainPendingIntent = PendingIntent.getActivity(context,
            0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_MUTABLE)

    private val detailIntent = Intent(context, DetailActivity::class.java)

    init {

        // NotificationChannel is required on Oreo and newer
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(NotificationChannel(
                    CHANNEL_ID,
                    context.getString(bez.dev.featurenotes.R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW))
        }

    }

    private fun setSummaryBuilder(): Notification {
        return mNotificationBuilder
                .setSmallIcon(bez.dev.featurenotes.R.mipmap.ic_app_launcher)
                .setColor(ContextCompat.getColor(context, bez.dev.featurenotes.R.color.colorPrimary))
                .setGroup(KEY_NOTIFICATION_GROUP)
                .setGroupSummary(true)
                .setContentIntent(mainPendingIntent)
                .build()
    }


    fun cancelNotifications() {
        mNotificationManager.cancelAll()
    }

    fun cancelNotificationById(noteId: Long) {
        mNotificationManager.cancel(noteId.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getNotificationCount(): Int {
        return mNotificationManager.activeNotifications.size
    }

    fun updateSpecificNotification(note: Note) {
        NotificationManagerCompat.from(context).apply {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
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
            NotificationManagerCompat.from(context).apply {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                notify(NOTIFICATION_ID, setSummaryBuilder())
            }
        } else {
            cancelNotifications()   //mainly for summary
        }
    }


    private fun createNotificationBuilder(note: Note): Notification {
        if(!note.isNotification) cancelNotificationById(note.id)

        // Create the RemoteInput specifying this key.
        val labelADD = "ADD"
        val labelDISMISS = "DISMISS"
        val remoteInput = RemoteInput.Builder(AddFromNotificationIntentService.EXTRA_REPLY)
                .setLabel(labelADD)
                .build()

        // Pending intent =
        //      API <24 (M and below): activity so the lock-screen presents the auth challenge.
        //      API 24+ (N and above): this should be a Service or BroadcastReceiver.
        var replyActionPendingIntent: PendingIntent? = null
        var dismissActionPendingIntent: PendingIntent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val intentAdd = Intent(context, AddFromNotificationIntentService::class.java)
            intentAdd.action = AddFromNotificationIntentService.ACTION_REPLY
            intentAdd.putExtra(AddFromNotificationIntentService.NOTIFICATION_NOTE, note)
            replyActionPendingIntent = PendingIntent.getService(context, note.id.toInt(), intentAdd, PendingIntent.FLAG_MUTABLE)

            val intentDismiss = Intent(context, AddFromNotificationIntentService::class.java)
            intentDismiss.action = AddFromNotificationIntentService.ACTION_DISMISS
            intentDismiss.putExtra(AddFromNotificationIntentService.NOTIFICATION_NOTE, note)
            dismissActionPendingIntent = PendingIntent.getService(context, note.id.toInt(), intentDismiss, PendingIntent.FLAG_MUTABLE)

        }

        val replyAction = NotificationCompat.Action.Builder(
                bez.dev.featurenotes.R.drawable.ic_add_white,
                labelADD,
                replyActionPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        val dismissAction = NotificationCompat.Action.Builder(
                bez.dev.featurenotes.R.drawable.ic_close,
                labelDISMISS,
                dismissActionPendingIntent)
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

        return setBodyNotification(note, replyAction, dismissAction, inboxStyle)
    }

    private fun setBodyNotification(note: Note, replyAction: NotificationCompat.Action?, dismissAction: NotificationCompat.Action?, inboxStyle: NotificationCompat.InboxStyle): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(bez.dev.featurenotes.R.mipmap.ic_app_launcher)
                .setContentTitle(boldText(note.title))
                .setGroup(KEY_NOTIFICATION_GROUP)
                .setOngoing(true)
                .setContentIntent(TaskStackBuilder.create(context).run {
                    // Add the intent, which inflates the back stack
                    detailIntent.putExtra(EXTRA_NOTE, note)
                    addNextIntentWithParentStack(detailIntent)
                    // Get the PendingIntent containing the entire back stack
                    getPendingIntent(Random.nextInt(), PendingIntent.FLAG_MUTABLE)
                })
                .addAction(replyAction)
                .addAction(dismissAction)
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
