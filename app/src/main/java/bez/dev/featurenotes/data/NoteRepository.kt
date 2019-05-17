package bez.dev.featurenotes.data

import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import bez.dev.featurenotes.misc.App
import java.lang.ref.WeakReference


class NoteRepository {
    private val noteDao: NoteDao = App.database.noteDao()
    val allNotes: LiveData<List<Note>>
    private val handler: Handler

    init {
        allNotes = noteDao.getAllNotesByPriority()

        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }
    fun insert(note: Note): Long {
        return InsertTask(this, noteDao).execute(note).get()
    }

    fun update(note: Note) {
        handler.post { noteDao.update(note) }
    }

    fun delete(note: Note) {
        handler.post { noteDao.delete(note) }
    }

    fun deleteAllNotes() {
        handler.post { noteDao.deleteAllNotes() }
    }

    fun resetAllNotifications() {
        handler.post { noteDao.resetAllNotifications() }
    }


    private class InsertTask(noteRepository: NoteRepository, private val noteDao: NoteDao) : AsyncTask<Note, Void, Long>() {

        private val repoReference: WeakReference<NoteRepository> = WeakReference(noteRepository)

        override fun doInBackground(vararg note: Note): Long {
            return noteDao.insert(note[0])
        }

        override fun onPostExecute(generatedId: Long) {
            // if no reference to the activity - then return
            repoReference.get() ?: return
            //otherwise, we have a repo reference so we proceed to return generatedId
            getId(generatedId)
        }

        private fun getId(generatedId: Long): Long {
            return generatedId
        }
    }
}

