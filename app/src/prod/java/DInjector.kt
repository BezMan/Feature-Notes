package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.INotesInit
import bez.dev.featurenotes.data.InitNotesProd

object DInjector {

    fun setInitNoteData(): INotesInit {
        return InitNotesProd()
    }

}
