package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.InitNotesMock
import bez.dev.featurenotes.data.NoteRepository

object DInjector {

    fun getNotes(): NoteRepository{
        return InitNotesMock()
    }

}
