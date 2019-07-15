package bez.dev.featurenotes.data

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MockNoteRepository : NoteRepository() {

    init {
        getSavedNotes()
    }


    override fun getSavedNotes() {
        if (SharedPrefs.getBoolValue(KEY_FIRST_RUN, true)) {
            clearAllData()
            insert(Note("mock1", 1, "[\"click edit\",\"drag and drop\"]"))
//                    insert(Note("mock2", 2, "[\"click edit\",\"drag and drop\"]"))
//                    insert(Note("mock3", 3, "[\"click edit\",\"drag and drop\"]"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

            //toggle to not first run anymore:
            SharedPrefs.setBoolValue(KEY_FIRST_RUN, false)
        }
    }

}