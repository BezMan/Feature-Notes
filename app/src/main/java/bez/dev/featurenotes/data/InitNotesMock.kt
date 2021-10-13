package bez.dev.featurenotes.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class InitNotesMock : KoinComponent {

    private val repository: NoteRepository by inject()
    private val initItems: Int = 5


    fun setInitNotes() {
        val noteList = mutableListOf<Note>()
        for (i in 1..initItems) {
            noteList.add( Note("mock $i", i, mutableListOf(
                    NoteItem("select edit"),
                    NoteItem("drag and drop"))))
        }

        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(noteList)
            }
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false) //toggle to not first run anymore:
        }
    }


}