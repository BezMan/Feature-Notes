package bez.dev.featurenotes

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import bez.dev.featurenotes.views.DetailActivity
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
    var detailActivity = ActivityTestRule(DetailActivity::class.java)


    @Test
    fun clickNoteAdd_openDetailActivityAddNoteTitleAndItem() {
        onView(withId(R.id.fab_add_note))
                .perform(click())

        onView(withId(R.id.edit_text_title))
                .perform(typeText(getTimeStamp()))


        val numItems = 1
        addItems(numItems)
    }

    @Test
    fun clickNoteEdit_openDetailActivityEditItem(){
        val title = "mock 2"
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(title)), click()));

        onView(withId(R.id.edit_text_title))
                .check(ViewAssertions.matches(withText(title)))

//  //click item by position//
//        onView(withId(R.id.recycler_view))
//                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()));
    }

    @Test
    fun clickOverflowMenuButton_addNote(){
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        onView(withText("Add Note"))
                .perform(click())

        onView(withId(R.id.edit_text_title))
                .perform(typeText(getTimeStamp()))



    }

    private fun getTimeStamp(): String{
        val formatter = SimpleDateFormat("h:mm a, d MMM")
        return formatter.format(Date(System.currentTimeMillis()))
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
