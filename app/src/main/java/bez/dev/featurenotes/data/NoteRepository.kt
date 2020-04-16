package bez.dev.featurenotes.data

import androidx.lifecycle.LiveData
import bez.dev.featurenotes.misc.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

open class NoteRepository : IRepository {
    private val noteDatabase: NoteDatabase = App.database
    private val noteDao: NoteDao = noteDatabase.noteDao()
    val allNotes: LiveData<List<Note>>
    private val repoScope = CoroutineScope(Dispatchers.IO)

    init {
        repoScope.launch {
            setInitNotes()
        }
        allNotes = noteDao.getAllNotesByPriority()
    }

     open suspend fun setInitNotes() {
         if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
             clearAllData()
             for (i in 1..5) {
                 insert(Note("mock $i", i, mutableListOf(NoteItem("select edit"), NoteItem("drag and drop"))))
             }
             //toggle to not first run anymore:
             SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
         }

     }

    override suspend fun insert(note: Note): Long {
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

    override fun getNote(note: Note): LiveData<Note> {
        return noteDao.getNote(note.id)
    }

}

