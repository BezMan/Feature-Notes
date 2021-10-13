package bez.dev.featurenotes.data

import androidx.lifecycle.LiveData
import bez.dev.featurenotes.misc.App
import bez.dev.featurenotes.misc.DInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

open class NoteRepository : IRepository {
    private val noteDatabase: NoteDatabase = App.database
    private val noteDao: NoteDao = noteDatabase.noteDao()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    init {
        DInjector.setInitNotes()
        noteDao.getAllNotesByPriority()
    }

    override fun insert(note: Note): Long {
        return noteDao.insert(note)
    }

    override fun insert(note: List<Note>): List<Long> {
        return noteDao.insert(note)
    }

    override fun update(note: Note) {
        repoScope.launch { noteDao.update(note) }
    }

    override fun delete(note: Note) {
        repoScope.launch { noteDao.delete(note) }
    }

    override fun deleteAllNotes() {
        repoScope.launch { noteDao.deleteAllNotes() }
    }

    override fun clearAllData() {
        repoScope.launch { noteDatabase.clearAllTables() }
    }

    override fun resetAllNotifications() {
        repoScope.launch { noteDao.resetAllNotifications() }
    }

    override fun getNoteById(noteId: Long): LiveData<Note> {
        return noteDao.getNoteById(noteId)
    }

    override fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotesByPriority()
    }

    override fun getArchivedNotes(): LiveData<List<Note>> {
        return noteDao.getAllArchivedNotesByPriority()
    }

}

