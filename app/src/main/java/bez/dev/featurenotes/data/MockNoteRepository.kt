package bez.dev.featurenotes.data

class MockNoteRepository : NoteRepository() {

    private val initItems: Int = 5


    override suspend fun setInitNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            clearAllData()
            for (i in 1..initItems) {
                insert(Note("mock $i", i, "[\"click edit\",\"drag and drop\"]"))
            }
            //toggle to not first run anymore:
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
        }
    }

}