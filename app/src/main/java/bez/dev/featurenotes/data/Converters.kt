package bez.dev.featurenotes.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun listToJson(value: MutableList<NoteItem>): String {
            return Gson().toJson(value)
        }

        @TypeConverter
        @JvmStatic
        fun jsonToList(value: String?): MutableList<NoteItem> {
            val type = object : TypeToken<MutableList<NoteItem>?>() {}.type
            return Gson().fromJson(value, type)
        }

    }

}
