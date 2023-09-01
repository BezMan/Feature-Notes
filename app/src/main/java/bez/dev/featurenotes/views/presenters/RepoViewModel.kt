package bez.dev.featurenotes.views.presenters

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bez.dev.featurenotes.data.domain.IRepository
import bez.dev.featurenotes.data.domain.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(private val repository: IRepository) : ViewModel() {

    suspend fun insert(note: Note): Long {
        return repository.insert(note)
    }

    fun update(note: Note) {
        repository.update(note)
    }

    fun archive(note: Note) {
        note.isArchived = true
        note.isNotification = false
        repository.update(note)
    }

    fun unArchive(note: Note) {
        note.isArchived = false
        repository.update(note)
    }

    fun delete(note: Note) = repository.delete(note)

    fun deleteAllNotes() = repository.deleteAllNotes()

    fun resetAllNotifications() = repository.resetAllNotifications()

    fun getNoteById(noteId: Long): LiveData<Note> = repository.getNoteById(noteId).asLiveData()

    fun getAllNotes(): LiveData<List<Note>> = repository.getAllNotes().asLiveData()

    fun getArchivedNotes(): LiveData<List<Note>> = repository.getArchivedNotes().asLiveData()

}