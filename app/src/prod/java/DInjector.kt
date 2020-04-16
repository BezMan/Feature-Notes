package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.INotesInit
import bez.dev.featurenotes.data.InitNotesProd
import bez.dev.featurenotes.data.NoteRepository

object DInjector {

    fun setInitNoteData(noteRepository: NoteRepository): INotesInit {
        return InitNotesProd(noteRepository)
    }

}
