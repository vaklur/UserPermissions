package com.example.userpermissions.sms_permission.settings

import android.content.Context
import android.content.SharedPreferences

/**
 * Functions for using [SharedPreferences] to manage server address.
 *
 * @property settingsFile The name of file for data storage.
 * @property sharedPreferences The object of [SharedPreferences].
 */
class SettingsSharPref(context: Context) {

    private var settingsFile = "settings"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(settingsFile, Context.MODE_PRIVATE)

    /**
     * Save the new server address.
     *
     * @param address URL of server address to save.
     */
    fun saveIPsettings(address:String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("ipAddress",address)
        editor.apply()
        editor.commit()

    }

    /**
     * Return the saved server address.
     *
     * @return Saved url address of server.
     */
    fun getIPsettings():String{
        return sharedPreferences.getString("ipAddress","http://unsecureapp.tode.cz").toString()
    }

    /**
     * Delete all saved server address.
     */
    fun clearIPsettings(){
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }

    /**
     * Return a list of all saved server address.
     *
     * @return List of all saved server address.
     */
    fun getIpsettingsSet(): MutableSet<String>? {
        val defaultSet = mutableSetOf<String>()
        defaultSet.add(getIPsettings())
        return sharedPreferences.getStringSet("ipAddressSet",defaultSet)
    }

    /**
     * Add address to list of server address.
     *
     * @param address New server address to save to the list.
     */
    fun addAddressToIpsettingsSet(address:String){
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val ipAddressSet = getIpsettingsSet()
        if (ipAddressSet?.size!! > 5){
            ipAddressSet.remove(ipAddressSet.first())
        }
        ipAddressSet.add(address)
        editor.putStringSet("ipAddressSet",ipAddressSet)
        editor.apply()
        editor.commit()

    }
}