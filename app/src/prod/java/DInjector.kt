package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.InitNotesProd

object DInjector {

    fun setInitNotes() {
        return InitNotesProd().setInitNotes()
    }

}
