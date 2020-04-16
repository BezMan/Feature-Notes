package bez.dev.featurenotes.data

import org.koin.core.KoinComponent

class InitNotesProd(private val noteRepository: NoteRepository) : INotesInit, KoinComponent {

    override suspend fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            noteRepository.clearAllData()
            noteRepository.insert(Note("This is a note", 1,
                    mutableListOf(
                            NoteItem("long click to copy"),
                            NoteItem("select edit, set priority"),
                            NoteItem("drag and drop"))))

            //toggle to not first run anymore:
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
        }
    }

}