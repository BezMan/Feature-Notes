import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import bez.dev.featurenotes.data.domain.Note
import bez.dev.featurenotes.views.presenters.RepoViewModel
import bez.dev.featurenotes.views.screens.BaseActivity
import kotlinx.coroutines.launch

class ArchiveFragment : Fragment() {

    private val repoViewModel: RepoViewModel by activityViewModels()
    private val baseActivity: BaseActivity by lazy { requireActivity() as BaseActivity }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ArchiveScreen()
            }
        }
    }

    @Composable
    fun ArchiveScreen() {
        val archivedNotes by repoViewModel.getArchivedNotes().collectAsState(emptyList())
        val coroutineScope = rememberCoroutineScope()
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState, // Pass scaffoldState to Scaffold
            topBar = {
                TopAppBar(
                    title = { Text(text = "Archive") },
                    actions = {
                        IconButton(onClick = { /* Handle action */ }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                    }
                )
            },
            content = {
                val modifier = Modifier.padding(it)
                Column(modifier) { // Apply the padding modifier to the Column
                    if (archivedNotes.isEmpty()) {
                        NoNotesView()
                    } else {
                        NoteList(
                            notes = archivedNotes,
                            onItemClick = { note -> baseActivity.editNote(note, true) },
                            onUnArchiveClick = { note -> showUndoArchiveRestore(note, scaffoldState) },
                            onOverflowClick = { note, overflow -> showPopupMenu(note, overflow, scaffoldState) }
                        )
                    }
                }
            }
        )
    }

    @Composable
    fun NoNotesView() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No archived notes",
                style = MaterialTheme.typography.h5,
                color = Color.Gray
            )
        }
    }

    @Composable
    fun NoteList(
        notes: List<Note>,
        onItemClick: (Note) -> Unit,
        onUnArchiveClick: (Note) -> Unit,
        onOverflowClick: (Note, OverflowMenu) -> Unit
    ) {
        LazyColumn {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onItemClick = { onItemClick(note) },
                    onUnArchiveClick = { onUnArchiveClick(note) },
                    onOverflowClick = { overflow -> onOverflowClick(note, overflow) }
                )
            }
        }
    }

    @Composable
    fun NoteItem(
        note: Note,
        onItemClick: () -> Unit,
        onUnArchiveClick: () -> Unit,
        onOverflowClick: (OverflowMenu) -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .clickable { onItemClick() }
                    .padding(16.dp)
            ) {
                Text(text = note.title, style = MaterialTheme.typography.h6)
                Text(text = note.title, style = MaterialTheme.typography.body2)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onUnArchiveClick() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                    OverflowMenu { overflow -> onOverflowClick(overflow) }
                }
            }
        }
    }

    @Composable
    fun OverflowMenu(onClick: (OverflowMenu) -> Unit) {
        var expanded by remember { mutableStateOf(false) }
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(onClick = { onClick(OverflowMenu.EDIT) }) {
                Text("Edit")
            }
            DropdownMenuItem(onClick = { onClick(OverflowMenu.SHARE) }) {
                Text("Share")
            }
            DropdownMenuItem(onClick = { onClick(OverflowMenu.DELETE) }) {
                Text("Delete")
            }
            DropdownMenuItem(onClick = { onClick(OverflowMenu.UNARCHIVE) }) {
                Text("Unarchive")
            }
        }
    }

    private fun showUndoArchiveRestore(note: Note, scaffoldState: ScaffoldState) {
        baseActivity.unArchiveNote(note)

        baseActivity.baseCoroutineIO.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "${note.title} - note unarchived",
                actionLabel = "UNDO",
                duration = SnackbarDuration.Short // Specify the duration here
            ).apply {
                // Execute when UNDO is clicked
                baseActivity.baseCoroutineIO.launch {
                    repoViewModel.archive(note)
                }
            }
        }
    }
    private fun showPopupMenu(note: Note, overflow: OverflowMenu, scaffoldState: ScaffoldState) {
        when (overflow) {
            OverflowMenu.EDIT -> baseActivity.editNote(note, true)
            OverflowMenu.SHARE -> baseActivity.shareNote(note)
            OverflowMenu.DELETE -> showUndoDelete(note, scaffoldState)
            OverflowMenu.UNARCHIVE -> showUndoArchiveRestore(note, scaffoldState)
        }
    }

    private fun showUndoDelete(note: Note, scaffoldState: ScaffoldState) {
        baseActivity.deleteNote(note)

        baseActivity.baseCoroutineIO.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "${note.title} - note deleted",
                actionLabel = "UNDO",
                duration = SnackbarDuration.Short // Specify the duration here
            ).apply {
                // Execute when UNDO is clicked
                baseActivity.baseCoroutineIO.launch {
                    repoViewModel.insert(note)
                }
            }
        }
    }

    enum class OverflowMenu {
        EDIT, SHARE, DELETE, UNARCHIVE
    }
}
