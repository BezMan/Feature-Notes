package bez.dev.featurenotes.data

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import bez.dev.featurenotes.misc.App
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

abstract class NoteRepository : IRepository {
    private val noteDatabase: NoteDatabase = App.database
    private val noteDao: NoteDao = noteDatabase.noteDao()
    val allNotes: LiveData<List<Note>>

    init {
        allNotes = noteDao.getAllNotesByPriority()
    }

    abstract fun getSavedNotes()


    override fun insert(note: Note): Long {
        return InsertTask(this, noteDao).execute(note).get()
    }

    override fun update(note: Note) {
        GlobalScope.launch { noteDao.update(note) }
    }

    override fun delete(note: Note) {
        GlobalScope.launch { noteDao.delete(note) }
    }

    override fun deleteAllNotes() {
        GlobalScope.launch { noteDao.deleteAllNotes() }
    }

    override fun clearAllData() {
        GlobalScope.launch { noteDatabase.clearAllTables() }
    }

    override fun resetAllNotifications() {
        GlobalScope.launch { noteDao.resetAllNotifications() }
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

