package bez.dev.featurenotes.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val KEY_FIRST_RUN = "KEY_FIRST_RUN"

class NoteRepository @Inject constructor(private val database: NoteDatabase, private val sharedPrefs: SharedPrefs) : IRepository {

    private val noteDao: NoteDao = database.noteDao()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    init {
        setNotesFirstRun()
        getAllNotes()
    }

    private fun setNotesFirstRun() {

        if (sharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                insert(createStarterNotes())
            }
            sharedPrefs.setBoolValue(KEY_FIRST_RUN, false) //toggle to not first run anymore:
        }
    }

    private fun createStarterNotes(noteCount: Int = 5): MutableList<Note> {
        val noteList = mutableListOf<Note>()
        for (i in 1..noteCount) {
            noteList.add(
                Note(
                    "mock $i", i, mutableListOf(
                        NoteItem("select edit"),
                        NoteItem("drag and drop")
                    )
                )
            )
        }
        return noteList
    }

    override suspend fun insert(note: Note): Long {
        val res = repoScope.async {
            noteDao.insert(note)
        }
        return res.await()
    }

    override suspend fun insert(note: List<Note>): List<Long> {
        val res = repoScope.async {
            noteDao.insert(note)
        }
        return res.await()
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
        repoScope.launch { database.clearAllTables() }
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

