package bez.dev.featurenotes.data

class RealNoteRepository : NoteRepository() {

    override suspend fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            clearAllData()
            insert(Note("This is a note", 1,
                    mutableListOf(
                            NoteItem("long click to copy"),
                            NoteItem("select edit, set priority"),
                            NoteItem("drag and drop"))))

            //toggle to not first run anymore:
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
        }
    }

}