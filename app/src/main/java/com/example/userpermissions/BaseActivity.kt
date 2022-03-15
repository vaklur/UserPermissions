package com.example.userpermissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.userpermissions.settings.LocaleUtil
import com.example.userpermissions.settings.SettingsSharPref

open class BaseActivity: AppCompatActivity() {
    private lateinit var oldPrefLocaleCode : String

    /**
     * updates the toolbar text locale if it set from the android:label property of Manifest
     */
    private fun resetTitle() {
        try {
            val label = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA).labelRes
            if (label != 0) {
                setTitle(label)
            }
        } catch (e: PackageManager.NameNotFoundException) {}
    }

    override fun attachBaseContext(newBase: Context) {
        oldPrefLocaleCode = SettingsSharPref(newBase).getLanguageSettings()
        applyOverrideConfiguration(LocaleUtil.getLocalizedConfiguration(oldPrefLocaleCode))
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetTitle()
    }

    override fun onResume() {
        val currentLocaleCode = SettingsSharPref(this).getLanguageSettings()
        if(oldPrefLocaleCode != currentLocaleCode){
            recreate()
            oldPrefLocaleCode = currentLocaleCode
        }
        super.onResume()
    }
}
