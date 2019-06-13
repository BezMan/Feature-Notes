package bez.dev.featurenotes.misc

import bez.dev.featurenotes.data.MockNoteRepository
import bez.dev.featurenotes.data.NoteRepository

object DInjector {

    fun getRepository(): NoteRepository {
        return MockNoteRepository()
    }

}