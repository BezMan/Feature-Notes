package bez.dev.featurenotes.data

import android.os.AsyncTask
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.LiveData
import bez.dev.featurenotes.misc.App
import java.lang.ref.WeakReference

const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

abstract class NoteRepository : IRepository {
    private val noteDatabase: NoteDatabase = App.database
    private val noteDao: NoteDao = noteDatabase.noteDao()
    val allNotes: LiveData<List<Note>>
    private val handler: Handler

    init {
        allNotes = noteDao.getAllNotesByPriority()

        val handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    abstract fun getSavedNotes()


    override fun insert(note: Note): Long {
        return InsertTask(this, noteDao).execute(note).get()
    }

    override fun update(note: Note) {
        handler.post { noteDao.update(note) }
    }

    override fun delete(note: Note) {
        handler.post { noteDao.delete(note) }
    }

    override fun deleteAllNotes() {
        handler.post { noteDao.deleteAllNotes() }
    }

    override fun clearAllData() {
        handler.post { noteDatabase.clearAllTables() }
    }

    override fun resetAllNotifications() {
        handler.post { noteDao.resetAllNotifications() }
    }

    override fun getNoteItems(note: Note): LiveData<String> {
        return GetNoteItems(this, noteDao).execute(note).get()
    }


    private class GetNoteItems(noteRepository: NoteRepository, private val noteDao: NoteDao) : AsyncTask<Note, Void, LiveData<String>>() {

        private val repoReference: WeakReference<NoteRepository> = WeakReference(noteRepository)

        override fun doInBackground(vararg note: Note): LiveData<String> {
            return noteDao.getNoteItems(note[0].id)
        }

        override fun onPostExecute(items: LiveData<String>) {
            // if no reference to the activity - then return
            repoReference.get() ?: return
            //otherwise, we have a repo reference so we proceed to return items
            getItems(items)
        }

        private fun getItems(items: LiveData<String>): LiveData<String> {
            return items
        }
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

