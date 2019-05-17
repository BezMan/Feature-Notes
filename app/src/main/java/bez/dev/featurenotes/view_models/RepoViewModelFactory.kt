@file:Suppress("UNCHECKED_CAST")

package bez.dev.featurenotes.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bez.dev.featurenotes.data.NoteRepository

class RepoViewModelFactory(private val noteRepository: NoteRepository) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RepoViewModel(noteRepository) as T
    }
}
