package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.INotesInit
import bez.dev.featurenotes.data.MockInitNotes

object DInjector {

    fun setInitNoteData(): INotesInit{
        return MockInitNotes()
    }

}
