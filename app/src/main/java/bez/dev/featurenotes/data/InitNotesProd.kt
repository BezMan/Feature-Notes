package bez.dev.featurenotes.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class InitNotesProd : KoinComponent {

    fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {

            CoroutineScope(Dispatchers.IO).launch {
                val repository: NoteRepository by inject()
                repository.clearAllData()
                repository.insert(Note("This is a note", 1,
                        mutableListOf(
                                NoteItem("long click to copy"),
                                NoteItem("select edit, set priority"),
                                NoteItem("drag and drop"))))
                //toggle to not first run anymore:
                SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
            }
        }

    }

}