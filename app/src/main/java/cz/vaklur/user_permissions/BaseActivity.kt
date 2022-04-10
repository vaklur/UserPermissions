package cz.vaklur.user_permissions

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.vaklur.user_permissions.settings.LocaleUtil
import cz.vaklur.user_permissions.settings.SettingsSharedPreferences

/**
 * Based activity which contains function for changing app language
 */
open class BaseActivity : AppCompatActivity() {

    private lateinit var oldPrefLocaleCode: String

    /**
     * Updates the toolbar text locale if it set from the android:label property of Manifest
     */
    private fun resetTitle() {
        title = getString(R.string.app_name)
    }

    /**
     * Attach base context depending on the language selected
     */
    override fun attachBaseContext(newBase: Context) {
        oldPrefLocaleCode = SettingsSharedPreferences(newBase).getLanguageSettings()
        applyOverrideConfiguration(LocaleUtil.getLocalizedConfiguration(oldPrefLocaleCode))
        super.attachBaseContext(newBase)
    }

    /**
     * Reset app title when call onCreate on activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitle()
    }

    /**
     * When the onResume is called, change application LocaleCode to actual saved in Shared Preferences
     */
    override fun onResume() {
        val currentLocaleCode = SettingsSharedPreferences(this).getLanguageSettings()
        if (oldPrefLocaleCode != currentLocaleCode) {
            recreate()
            oldPrefLocaleCode = currentLocaleCode
        }
        super.onResume()
    }
}
