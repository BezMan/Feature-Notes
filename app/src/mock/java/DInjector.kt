package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.InitNotesMock

object DInjector {

    fun setInitNotes() {
        return InitNotesMock().setInitNotes()
    }

}
