package bez.dev.featurenotes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import bez.dev.featurenotes.views.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class NotesTest {

    @get:Rule
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    private val timeStamp: String
        get() {
            val formatter = SimpleDateFormat("h:mm a, d MMM")
            return formatter.format(Date(System.currentTimeMillis()))

        }

    @Test
    fun clickNoteAdd_openDetailActivityAddNoteTitleAndItem() {
        onView(withId(R.id.main_menu_add_note))
                .perform(click())

        onView(withId(R.id.edit_text_title))
                .perform(typeText(timeStamp))


        val numItems = 1
        addItems(numItems)
    }


    private fun addItems(numItems: Int) {

        for (i in 0 until numItems) {
            onView(withId(R.id.top_add_item_btn))
                    .perform(click())

            onView(withId(R.id.dialogEditText))
                    .perform(typeText(String.format("item %d", i + 1)))

            onView(withId(R.id.dialogSaveTextBtn))
                    .perform(click())
        }

    }


}
