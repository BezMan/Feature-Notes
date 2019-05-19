package bez.dev.featurenotes.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import bez.dev.featurenotes.data.Note
import bez.dev.featurenotes.data.NoteRepository

class RepoViewModel(private val repository: NoteRepository) : ViewModel() {
    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun insert(note: Note): Long {
        return repository.insert(note)
    }

    fun update(note: Note) {
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

    fun getNoteItems(note: Note): LiveData<String> {
        return repository.getNoteItems(note)
    }
}