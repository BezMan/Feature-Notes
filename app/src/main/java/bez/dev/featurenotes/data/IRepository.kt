package bez.dev.featurenotes.data

import androidx.lifecycle.LiveData
import io.reactivex.Completable

interface IRepository {

    fun insert(note: Note): Completable
    fun update(note: Note)
    fun delete(note: Note)
    fun deleteAllNotes()
    fun clearAllData()
    fun resetAllNotifications()
    fun getNoteItems(note: Note): LiveData<String>

}
