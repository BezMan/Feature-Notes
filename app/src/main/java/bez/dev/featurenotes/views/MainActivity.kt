package bez.dev.featurenotes.views

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import bez.dev.featurenotes.R
import bez.dev.featurenotes.databinding.MainActivityBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var _binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = MainActivityBinding.inflate(layoutInflater)

        if (isCalledFromSummaryNotification()) return

        setContentView(_binding.root)

        initUI(savedInstanceState)

        handleBackPress()
    }

    private fun handleBackPress() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (_binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    _binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (!_binding.navView.menu.getItem(0).isChecked){
                    // if (R.id.nav_notes) NOT selected, go to it:
                    showNotesFragment()
                } else {
                    moveTaskToBack(true)
                }
            }
        })

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
        setSupportActionBar(_binding.customToolbar.mainListToolbar)    //merges the custom TOOLBAR with the existing MENU

        //NAVIGATION DRAWER
        val toggle = ActionBarDrawerToggle(this, _binding.drawerLayout, _binding.customToolbar.mainListToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        _binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        _binding.navView.setNavigationItemSelectedListener(this)


        if (savedInstanceState == null) { //don't trigger on device rotation
            showNotesFragment()
        }
    }

    fun setToolbarText(text: CharSequence){
        _binding.customToolbar.toolbarMainText.text = text
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if(item.itemId != _binding.navView.checkedItem?.itemId) {
            when (item.itemId) {
                R.id.nav_notes -> {
                    replaceFragment(R.id.fragment_container, NotesFragment())
                }
                R.id.nav_archive -> {
                    replaceFragment(R.id.fragment_container, ArchiveFragment())
                }
                R.id.nav_settings -> {
                    openSettings()
                }
            }
        }
        _binding.drawerLayout.closeDrawer(GravityCompat.START)
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


    private fun showNotesFragment() {
        replaceFragment(R.id.fragment_container, NotesFragment())
        _binding.navView.setCheckedItem(R.id.nav_notes)
    }

}
