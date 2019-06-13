package bez.dev.featurenotes.data

import androidx.lifecycle.LiveData

interface IRepository {

    fun insert(note: Note): Long
    fun update(note: Note)
    fun delete(note: Note)
    fun deleteAllNotes()
    fun clearAllData()
    fun resetAllNotifications()
    fun getNoteItems(note: Note): LiveData<String>

}