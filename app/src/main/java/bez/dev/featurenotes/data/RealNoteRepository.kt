package bez.dev.featurenotes.data

class RealNoteRepository : NoteRepository() {

    override suspend fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            clearAllData()
            insert(Note("swipe to delete", 1, "[\"click edit\",\"drag and drop\"]"))

            //toggle to not first run anymore:
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
        }
    }

}