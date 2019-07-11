package bez.dev.featurenotes.data

import androidx.lifecycle.LiveData
import bez.dev.featurenotes.misc.App
import io.reactivex.Completable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

abstract class NoteRepository : IRepository {
    private val noteDatabase: NoteDatabase = App.database
    private val noteDao: NoteDao = noteDatabase.noteDao()
    val allNotes: LiveData<List<Note>>


    init {
        allNotes = noteDao.getAllNotesByPriority()
    }

    abstract fun getSavedNotes()


    override fun insert(note: Note): Completable {
        return noteDao.insert(note)
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
        return noteDao.getNoteItems(note.id)
    }

}

