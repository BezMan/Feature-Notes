package bez.dev.featurenotes.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import bez.dev.featurenotes.R
import bez.dev.featurenotes.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var _binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()

        //TOOLBAR
        setSupportActionBar(_binding.customToolbar.mainListToolbar)    //merges the custom TOOLBAR with the existing MENU
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        _binding.customToolbar.toolbarMainText.text = resources.getText(R.string.nav_settings)
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

//            handlePreferences()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

}