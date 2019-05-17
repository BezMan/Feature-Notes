package bez.dev.featurenotes.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    companion object {

        @TypeConverter
        @JvmStatic
        fun listToJson(value: MutableList<String>): String {
            return Gson().toJson(value)
        }

        @TypeConverter
        @JvmStatic
        fun jsonToList(value: String?): MutableList<String> {
            var objects: Array<String> = emptyArray()
            try {
                objects = Gson().fromJson(value, Array<String>::class.java)
            } catch (t: Throwable) {

            } finally {
                return objects.toMutableList()
            }
        }

    }

}
