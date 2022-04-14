package cz.vaklur.user_permissions.settings

import android.content.Context
import android.content.SharedPreferences
import cz.vaklur.user_permissions.constants.Constants

/**
 * Functions for using [SharedPreferences] to manage server address.
 *
 * @property sharedPreferences The object of [SharedPreferences].
 */
class SettingsSharedPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)

    /**
     * Save the new server address.
     *
     * @param address URL of server address to save.
     */
    fun saveIpSettings(address: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString(Constants.SHARED_PREFERENCES_IP_ADDRESS, address)
        editor.apply()
    }


    /**
     * Save the actual application language.
     *
     * @param language Actual application language to save.
     */
    fun saveLanguageSettings(language: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString(Constants.SHARED_PREFERENCES_LANGUAGE, language)
        editor.apply()
    }

    /**
     * Return the saved application language.
     *
     * @return Saved application language.
     */
    fun getLanguageSettings(): String {
        return sharedPreferences.getString(
            Constants.SHARED_PREFERENCES_LANGUAGE,
            Constants.DEFAULT_APP_LANGUAGE
        ).toString()
    }

    /**
     * Return the saved server address.
     *
     * @return Saved url address of server.
     */
    fun getIpSettings(): String {
        return sharedPreferences.getString(
            Constants.SHARED_PREFERENCES_IP_ADDRESS,
            Constants.DEFAULT_SERVER_ADDRESS
        ).toString()
    }

    /**
     * Return a list of all saved server address.
     *
     * @return List of all saved server address.
     */
    fun getIpSettingsSet(): MutableSet<String>? {
        val defaultSet = mutableSetOf<String>()
        defaultSet.add(getIpSettings())
        return sharedPreferences.getStringSet(
            Constants.SHARED_PREFERENCES_IP_ADDRESS_LIST,
            defaultSet
        )
    }

    /**
     * Add address to list of server address.
     *
     * @param address New server address to save to the list.
     */
    fun addAddressToIpSettingsSet(address: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val ipAddressSet = getIpSettingsSet()
        if (ipAddressSet?.size!! > 5) {
            ipAddressSet.remove(ipAddressSet.first())
        }
        ipAddressSet.add(address)
        editor.putStringSet(Constants.SHARED_PREFERENCES_IP_ADDRESS_LIST, ipAddressSet)
        editor.apply()
    }

    /**
     * Saved a user created state.
     */
    fun addUserCreatedState (state:Boolean){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putBoolean(Constants.SHARED_PREFERENCES_USER_CREATED, state)
        editor.apply()
    }

    /**
     * Get a user created state.
     */
    fun getUserCreatedState(): Boolean {
        return sharedPreferences.getBoolean(
            Constants.SHARED_PREFERENCES_USER_CREATED,
            Constants.DEFAULT_USER_CREATED
        )
    }
}