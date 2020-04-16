package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.INotesInit
import bez.dev.featurenotes.data.ProdInitNotes

object DInjector {

    fun setInitNoteData(): INotesInit {
        return ProdInitNotes()
    }

}
