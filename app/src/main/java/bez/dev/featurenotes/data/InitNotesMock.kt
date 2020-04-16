package bez.dev.featurenotes.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class InitNotesMock: KoinComponent{

    private val initItems: Int = 5


    fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {

            CoroutineScope(Dispatchers.IO).launch {
                val repository : NoteRepository by inject()
                repository.clearAllData()
                for (i in 1..initItems) {
                    repository.insert(Note("mock $i", i, mutableListOf(NoteItem("select edit"), NoteItem("drag and drop"))))
                }
                //toggle to not first run anymore:
                SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
            }
        }
    }

}