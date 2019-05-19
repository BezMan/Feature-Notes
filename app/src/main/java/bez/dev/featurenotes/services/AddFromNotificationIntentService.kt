/*
Copyright 2016 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package bez.dev.featurenotes.services

import android.app.IntentService
import android.content.Intent
import androidx.core.app.RemoteInput
import bez.dev.featurenotes.data.Converters
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.misc.App

class AddFromNotificationIntentService : IntentService("AddFromNotificationIntentService") {

    override fun onHandleIntent(intent: Intent) {
        if (intent.action == ACTION_REPLY) {
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
            val list = Converters.jsonToList(note.items)
            list.add(0, replyCharSequence.toString())
            note.items = Converters.listToJson(list)
        }
        App.database.noteDao().update(note) // must update() to notify(), even when empty.

    }


    companion object {
        const val ACTION_REPLY = "ACTION_REPLY"
        const val EXTRA_REPLY = "EXTRA_REPLY"
        const val NOTIFICATION_NOTE = "NOTIFICATION_NOTE"
    }


}
