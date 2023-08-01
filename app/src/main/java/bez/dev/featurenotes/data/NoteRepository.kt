package bez.dev.featurenotes.data

import bez.dev.featurenotes.misc.App
import bez.dev.featurenotes.misc.DInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

class NoteRepository : IRepository {

    private val noteDao: NoteDao = App.database.noteDao()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    init {
        DInjector.setInitNotes()
        getAllNotes()
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
        noteDao.delete(note)
    }

    override fun deleteAllNotes() {
        repoScope.launch { noteDao.deleteAllNotes() }
    }

    override fun clearAllData() {
        App.database.clearAllTables()
    }

    override fun resetAllNotifications() {
        repoScope.launch { noteDao.resetAllNotifications() }
    }

    override fun getNoteById(noteId: Long): Flow<Note> {
        return noteDao.getNoteById(noteId)
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotesByPriority()
    }

    override fun getArchivedNotes(): Flow<List<Note>> {
        return noteDao.getAllArchivedNotesByPriority()
    }

}

