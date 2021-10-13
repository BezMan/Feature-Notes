package bez.dev.featurenotes.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class InitNotesProd : KoinComponent {

    private val repository: NoteRepository by inject()

    fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {

            val items = mutableListOf(
                    NoteItem("long click to copy"),
                    NoteItem("select edit, set priority"),
                    NoteItem("drag and drop"))

            val note = Note("This is a note", 1, items)

            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(note)
            }
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false) //toggle to not first run anymore:
        }

    }

}