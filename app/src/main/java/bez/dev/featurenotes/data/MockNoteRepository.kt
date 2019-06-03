package bez.dev.featurenotes.data

import bez.dev.featurenotes.misc.App

class MockNoteRepository : NoteRepository() {

    init {
        getSavedNotes()
    }


    override fun getSavedNotes() {
        if (App.prefs.getBoolean(KEY_FIRST_RUN, true)) {
            clearAllData()
            insert(Note("mock1", 1, "[\"click edit\",\"drag and drop\"]"))
            insert(Note("mock2", 2, "[\"click edit\",\"drag and drop\"]"))
            insert(Note("mock3", 3, "[\"click edit\",\"drag and drop\"]"))
            //toggle to not first run anymore:
            App.prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
        }
    }

}