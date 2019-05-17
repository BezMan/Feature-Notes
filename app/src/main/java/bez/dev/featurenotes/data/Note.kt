package bez.dev.featurenotes.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import bez.dev.featurenotes.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class Note(var title: String
                , var priority: Int = R.integer.default_priority
                , var items: String = ""
                , var isNotification: Boolean = false
                , var numItems: Int = 0
                , var color: Int = 0
                , var timeCreated: Long = System.currentTimeMillis()
                , var timeModified: Long = System.currentTimeMillis()
                , var isArchived: Boolean = false
                , @PrimaryKey(autoGenerate = true) var id: Long = 0

) : Parcelable {


    override fun toString(): String {
        return "title: $title, items: $items\n"
    }
}

