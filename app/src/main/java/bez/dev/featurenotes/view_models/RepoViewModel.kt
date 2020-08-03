package bez.dev.featurenotes.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.data.NoteRepository

class RepoViewModel(private val repository: NoteRepository) : ViewModel() {

    suspend fun insert(note: Note): Long {
        return repository.insert(note)
    }

    fun update(note: Note) {
        repository.update(note)
    }

    fun archive(note: Note) {
        note.isArchived = true
        repository.update(note)
    }

    fun unArchive(note: Note) {
        note.isArchived = false
        repository.update(note)
    }

    fun delete(note: Note) {
        repository.delete(note)
    }

    fun deleteAllNotes() {
        repository.deleteAllNotes()
    }

    fun resetAllNotifications() {
        repository.resetAllNotifications()
    }

    fun getNoteById(noteId: Long): LiveData<Note> {
        return repository.getNoteById(noteId)
    }

    fun getAllNotes(): LiveData<List<Note>>{
        return repository.getAllNotes()
    }

    fun getArchivedNotes(): LiveData<List<Note>>{
        return repository.getArchivedNotes()
    }
}