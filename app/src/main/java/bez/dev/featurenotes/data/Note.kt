package bez.dev.featurenotes.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class Note(var title: String = ""
                , var priority: Int = -1
                , var items: MutableList<NoteItem> = mutableListOf()
                , var isNotification: Boolean = false
                , var numItems: Int = 0
                , var color: Int = 0
                , var timeCreated: Long = System.currentTimeMillis()
                , var timeModified: Long = System.currentTimeMillis()
                , var isArchived: Boolean = false
                , @PrimaryKey(autoGenerate = true) var id: Long = 0

) : Parcelable {

    override fun toString() = "$title:\n $items"

}


@Parcelize
data class NoteItem(var itemText: String, var isDone: Boolean = false)
    : Parcelable {

    override fun toString(): String {
        if (isDone) {
            return "\n* $itemText(DONE)"
        }
        return "\n* $itemText"
    }

}



