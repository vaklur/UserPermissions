package cz.vaklur.user_permissions.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Functions for using [SharedPreferences] to manage server address.
 *
 * @property settingsFile The name of file for data storage.
 * @property sharedPreferences The object of [SharedPreferences].
 */
class SettingsSharPref(context: Context) {

    private val defaultAddress = "https://vaklur.cz/permissions"
    private val defaultLanguage = "en"
    private var settingsFile = "settings"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(settingsFile, Context.MODE_PRIVATE)

    /**
     * Save the new server address.
     *
     * @param address URL of server address to save.
     */
    fun saveIpSettings(address:String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("ipAddress",address)
        editor.apply()
    }


    /**
     * Save the actual application language.
     *
     * @param language Actual application language to save.
     */
    fun saveLanguageSettings(language:String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("language",language)
        editor.apply()
    }

    /**
     * Return the saved application language.
     *
     * @return Saved application language.
     */
    fun getLanguageSettings():String{
        return sharedPreferences.getString("language",defaultLanguage).toString()
    }

    /**
     * Return the saved server address.
     *
     * @return Saved url address of server.
     */
    fun getIpSettings():String{
        return sharedPreferences.getString("ipAddress",defaultAddress).toString()
    }

    /**
     * Return a list of all saved server address.
     *
     * @return List of all saved server address.
     */
    fun getIpSettingsSet(): MutableSet<String>? {
        val defaultSet = mutableSetOf<String>()
        defaultSet.add(getIpSettings())
        return sharedPreferences.getStringSet("ipAddressSet",defaultSet)
    }

    /**
     * Add address to list of server address.
     *
     * @param address New server address to save to the list.
     */
    fun addAddressToIpSettingsSet(address:String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val ipAddressSet = getIpSettingsSet()
        if (ipAddressSet?.size!! > 5){
            ipAddressSet.remove(ipAddressSet.first())
        }
        ipAddressSet.add(address)
        editor.putStringSet("ipAddressSet",ipAddressSet)
        editor.apply()
    }
}