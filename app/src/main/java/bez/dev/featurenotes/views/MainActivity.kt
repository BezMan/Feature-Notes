package bez.dev.featurenotes.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import bez.dev.featurenotes.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_activity_toolbar.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isCalledFromSummaryNotification()) return

        setContentView(R.layout.main_activity)

        initUI(savedInstanceState)

    }

    /**
     * summary notification click - should RESUME app and not start another MainActivity
     */
    private fun isCalledFromSummaryNotification(): Boolean {
        if (!isTaskRoot
        //                && intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action != null && intent.action.equals(Intent.ACTION_MAIN)
        ) {
            finish()
            return true
        }
        return false
    }

    private fun initUI(savedInstanceState: Bundle?) {
        //TOOLBAR
        setSupportActionBar(main_list_toolbar)    //merges the custom TOOLBAR with the existing MENU

        //NAVIGATION DRAWER
        val toggle = ActionBarDrawerToggle(this, drawer_layout, main_list_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)


        if (savedInstanceState == null) { //don't do on device rotation
            replaceFragment(R.id.fragment_container, NotesFragment.newInstance())
            nav_view.setCheckedItem(R.id.nav_notes)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId != nav_view.checkedItem?.itemId) {
            when (item.itemId) {
                R.id.nav_notes -> {
                    replaceFragment(R.id.fragment_container, NotesFragment.newInstance())
                }
                R.id.nav_archive -> {
                    replaceFragment(R.id.fragment_container, ArchiveFragment.newInstance())
                }
                R.id.nav_settings -> {
                    openSettings()
                }
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private inline fun FragmentManager.doTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    private fun AppCompatActivity.replaceFragment(frameId: Int, fragment: Fragment) {
        supportFragmentManager.doTransaction { replace(frameId, fragment) }
    }
//
//    private fun AppCompatActivity.addFragment(frameId: Int, fragment: Fragment){
//        supportFragmentManager.doTransaction {
//            addToBackStack(null)
//            add(frameId, fragment)
//        }
//    }
//
//    private fun AppCompatActivity.removeFragment(fragment: Fragment) {
//        supportFragmentManager.doTransaction{
//            remove(fragment)
//        }
//        supportFragmentManager.popBackStack()
//    }


    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (!nav_view.menu.getItem(0).isChecked){
            // if (R.id.nav_notes) NOT selected, go to it:
            replaceFragment(R.id.fragment_container, NotesFragment.newInstance())
            nav_view.setCheckedItem(R.id.nav_notes)
        } else {
            moveTaskToBack(true)
        }
    }

}
