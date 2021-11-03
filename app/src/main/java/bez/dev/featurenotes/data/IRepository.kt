package bez.dev.featurenotes.data

import kotlinx.coroutines.flow.Flow

interface IRepository {

    fun insert(note: Note): Long
    fun insert(note: List<Note>): List<Long>
    fun update(note: Note)
    fun delete(note: Note)
    fun deleteAllNotes()
    fun clearAllData()
    fun resetAllNotifications()
    fun getNoteById(noteId: Long): Flow<Note>
    fun getAllNotes(): Flow<List<Note>>
    fun getArchivedNotes(): Flow<List<Note>>

}
