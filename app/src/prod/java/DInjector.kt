package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.NoteRepository
import bez.dev.featurenotes.data.RealNoteRepository

object DInjector {

    fun getRepository(): NoteRepository {
        return RealNoteRepository()
    }

}
