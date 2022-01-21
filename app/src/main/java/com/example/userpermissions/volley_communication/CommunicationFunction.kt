package com.example.userpermissions.volley_communication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.userpermissions.R
import org.json.JSONException

/**
 * Functions for communication with server.
 */
class CommunicationFunction {
    /*
    /**
     * Interface for test connection to server.
     */
    interface VolleyStringResponse {
        fun onSuccess()
        fun onError()
    }

    /**
     * Get the ID of android device.
     *
     * @param contentResolver Provides applications access to the content model.
     * @return ID of android device.
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(contentResolver: ContentResolver):String{
        var androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        androidId = androidId.substring(0,6)
        return androidId
    }

    /**
     * Add a sms to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param smsList Sms to send to server.
     */
    fun addSMStoServer(activity:Activity,smsList:MutableList<MySms>) {
         val username = "sms"+getAndroidId(activity.contentResolver)

         for (i in smsList.size-1 downTo 0 step 1) {
             val stringRequest = object : StringRequest(
                     Method.POST, getServerAddress(EndPoints.URL_ADD_SMS,activity),
                     Response.Listener<String> { response ->
                         try {
                         } catch (e: JSONException) {
                             e.printStackTrace()
                         }
                     },

                     Response.ErrorListener { }) {
                 @Throws(AuthFailureError::class)
                 override fun getParams(): Map<String, String> {
                     val params = HashMap<String, String>()
                     params["username"] = username
                     params["date"] = smsList[i].date
                     params["number"] = smsList[i].number
                     params["text"] = smsList[i].text
                     params["type"] = smsList[i].type
                     return params
                 }
             }

             VolleySingleton.instance?.addToRequestQueue(stringRequest)
         }
    }

    /**
     * Create a new user in SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     */
    fun createUserInServer(activity:Activity) {
        val username = "sms"+getAndroidId(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_CREATE_USER,activity),
                Response.Listener<String> { response ->
                    try {

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { Log.d("problem","no send")}) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Delete user in SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     */
    fun deleteUserInServer(activity:Activity) {
        val username = "sms"+getAndroidId(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_DELETE_USER,activity),
                Response.Listener<String> { response ->
                    try {
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Test connection to server with Volley library.
     *
     * @param testAddress Address of server.
     * @param volleyResponse Response of Volley communication.
     */
    fun testConnectionToServer (testAddress:String,volleyResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
                Method.GET, "$testAddress/v1/?op=serverstate",
                Response.Listener<String> { response ->
                    try {
                        volleyResponse.onSuccess()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener {
                        volleyResponse.onError()
                }) {


            }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Test connection to server.
     *
     * @param activity Activity for access to [SettingsSharPref].
     * @param view View for display [serverAlertDialog].
     */
    fun connectionToServer(activity: Activity, view: View){
        val settingsSP = SettingsSharPref(activity.applicationContext)
        testConnectionToServer(settingsSP.getIPsettings(), object: VolleyStringResponse {
            override fun onSuccess() {
                //DO NOTHING
            }
            override fun onError() {
                serverAlertDialog(view,activity)
            }
        })
    }

    /**
     * Dialog that appears when the server is not available.
     *
     * @param activity Activity for start [AppSettings].
     * @param view View for display a dialog.
     */
    private fun serverAlertDialog(view: View,activity: Activity) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.server_dialog_title)
        builder.setMessage(R.string.server_dialog_message)

        builder.setPositiveButton(
                R.string.server_dialog_yes) { dialog, id ->
            val settingsIntent = Intent(activity.applicationContext, AppSettings::class.java)
            activity.startActivity(settingsIntent)
        }

        builder.setNegativeButton(
                R.string.server_dialog_no) { dialog, id ->
        }

        builder.show()
    }

    /**
     * Get the whole url command server address.
     *
     * @param urlType Type of the server command.
     * @param activity Activity for access to [SettingsSharPref].
     * @return Whole url for command.
     */
    fun getServerAddress (urlType:String,activity: Activity):String {
         val settingsSP = SettingsSharPref(activity.applicationContext)
        val url_root = settingsSP.getIPsettings()
         val url_command = when(urlType){
             "add_sms" -> url_root+"/v1/?op=addsms"
             "create_user" -> url_root+"/v1/?op=createuser"
             "delete_user" -> url_root+"/v1/?op=deleteuser"
             "login_user" -> url_root+"/view/view_data.php"
             else -> url_root
         }
        return url_command
    }*/

}