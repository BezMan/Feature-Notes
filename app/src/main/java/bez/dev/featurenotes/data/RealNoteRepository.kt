package bez.dev.featurenotes.data

import bez.dev.featurenotes.misc.App

class RealNoteRepository : NoteRepository() {

    init {
        getSavedNotes()
    }

    override fun getSavedNotes() {
        if (App.prefs.getBoolean(KEY_FIRST_RUN, true)) {
            clearAllData()
            insert(Note("swipe to delete", 1, "[\"click edit\",\"drag and drop\"]"))
            //toggle to not first run anymore:
            App.prefs.edit().putBoolean(KEY_FIRST_RUN, false).apply()
        }
    }

}