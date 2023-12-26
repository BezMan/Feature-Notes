package bez.dev.featurenotes.data.db

import androidx.room.*
import bez.dev.featurenotes.data.domain.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: List<Note>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("UPDATE note_table SET isNotification = 0 WHERE isNotification = 1 ")
    fun resetAllNotifications()

    @Query("SELECT * FROM note_table WHERE isArchived = 0 ORDER BY priority DESC, timeModified DESC")
    fun getAllNotesByPriority(): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE isArchived = 1 ORDER BY priority DESC, timeModified DESC")
    fun getAllArchivedNotesByPriority(): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE id = :noteId")
    fun getNoteById(noteId: Long): Flow<Note>
}

