package bez.dev.featurenotes

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import bez.dev.featurenotes.views.MainActivity
import org.hamcrest.Matchers.`is`
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*


class NotesTest {

    @get:Rule
    var mainActivityTestRule = ActivityTestRule(MainActivity::class.java)
    var runningActivity: Activity? = null

    private val notesToAdd: Int = 3
    private val itemsToAdd: Int = 1

    @Test
    fun clickFab_openNewNote_addItems() {

        onView(withId(R.id.fab_add_note)).perform(click())

        addItems(itemsToAdd)

        pressBack()
        pressBack()

    }

    @Test
    fun addNotes() {

        val recyclerView = mainActivityTestRule.activity.findViewById<RecyclerView>(R.id.recycler_view)
        val noteCount = getRecyclerItemCount(recyclerView)

        for (i in 1..notesToAdd) {
            clickFab_openNewNote_addItems()
        }
        onView(withId(R.id.recycler_view)).check(RecyclerViewItemCountAssertion(noteCount + notesToAdd))
    }


    @Test
    fun clickNoteEdit_openDetailActivityEditItem() {
//        val title = "test mock"
//        onView(withId(R.id.recycler_view))
//                .perform(RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(title)), click()));
//
//        onView(withId(R.id.edit_text_title))
//                .check(matches(withText(title)))

//  //click item by position//
//        onView(withId(R.id.recycler_view))
//                .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()));
    }

    @Test
    fun clickOverflowMenuButton_addNote() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        onView(withText("Add Note"))
                .perform(click())

        onView(withId(R.id.edit_text_title))
                .perform(typeText(getTimeStamp()))


    }

    private fun getTimeStamp(): String {
        val formatter = SimpleDateFormat("h:mm a, d MMM")
        return formatter.format(Date(System.currentTimeMillis()))
    }


    private fun addItems(numItems: Int) {
        val detailRecyclerView = getActivityInstance()!!.findViewById<RecyclerView>(R.id.recycler_view_detail)
        val itemCount = getRecyclerItemCount(detailRecyclerView)

        for (i in 0 until numItems) {
            onView(withId(R.id.top_add_item_btn))
                    .perform(click())

            onView(withId(R.id.dialogEditText))
                    .perform(typeText(String.format("item %d", i + 1)))

            onView(withId(R.id.dialogSaveTextBtn))
                    .perform(click())
        }
        onView(withId(R.id.recycler_view_detail)).check(RecyclerViewItemCountAssertion(itemCount + numItems))
    }


    private fun getRecyclerItemCount(recyclerView: RecyclerView): Int {
        return recyclerView.adapter!!.itemCount
    }


    private fun getActivityInstance(): Activity? {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val resumedActivities: Collection<*> = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                runningActivity = resumedActivities.iterator().next() as Activity
            }
        }
        return runningActivity
    }


    class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {
        override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            view as RecyclerView
            assertThat(view.adapter?.itemCount, `is`(expectedCount))
        }
    }


}
