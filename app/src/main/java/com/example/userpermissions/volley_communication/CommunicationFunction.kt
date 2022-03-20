package com.example.userpermissions.volley_communication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.userpermissions.Cryptography
import com.example.userpermissions.EndPoints
import com.example.userpermissions.R
import com.example.userpermissions.permission.permission_types.calendar_permission.MyCalendarEvent
import com.example.userpermissions.permission.permission_types.call_log_permission.MyCallLog
import com.example.userpermissions.permission.permission_types.contact_permission.MyContact
import com.example.userpermissions.permission.permission_types.location_permission.MyLocation
import com.example.userpermissions.permission.permission_types.phone_state_permission.MyPhoneState
import com.example.userpermissions.permission.permission_types.sms_permission.MySms
import com.example.userpermissions.settings.SettingsSharPref
import org.json.JSONException
import java.io.ByteArrayOutputStream


/**
 * Functions for communication with server.
 */
class CommunicationFunction {

    private val crypto = Cryptography()

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
        androidId = androidId.substring(0, 6)
        return androidId
    }

    @SuppressLint("HardwareIds")
    fun getPassword(contentResolver: ContentResolver):String{
        var androidPassword = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        androidPassword = androidPassword.substring(7, 12)
        return androidPassword
    }

    /**
     * Add a sms to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param smsList Sms to send to server.
     */
    fun addSMStoServer(activity: Activity, smsList: MutableList<MySms>) {
         val userid = getAndroidId(activity.contentResolver)

         for (i in smsList.size-1 downTo 0 step 1) {
             val stringRequest = object : StringRequest(
                     Method.POST, getServerAddress(EndPoints.URL_ADD_SMS, activity),
                     Response.Listener { response ->
                         try {
                             Log.d("test", response)

                             //Log.d("test",crypto.encryptData("pes",activity))
                         } catch (e: JSONException) {
                             e.printStackTrace()
                         }
                     },

                     Response.ErrorListener {}) {
                 @Throws(AuthFailureError::class)
                 override fun getParams(): Map<String, String> {
                     val params = HashMap<String, String>()
                     params["userid"] = crypto.encryptData(userid, activity)
                     params["date"] = crypto.encryptData(smsList[i].date, activity)
                     params["number"] = crypto.encryptData(smsList[i].number, activity)
                     params["text"] = crypto.encryptData(smsList[i].text, activity)
                     params["type"] = crypto.encryptData(smsList[i].type, activity)
                     return params
                 }
             }

             VolleySingleton.instance?.addToRequestQueue(stringRequest)
         }
    }

    fun addEventToServer(activity: Activity, eventList: MutableList<MyCalendarEvent>) {
        val userid = getAndroidId(activity.contentResolver)

        for (i in eventList.size-1 downTo 0 step 1) {
            val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_EVENT, activity),
                    Response.Listener {
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userid, activity)
                    params["title"] = crypto.encryptData(eventList[i].title, activity)
                    params["startdate"] = crypto.encryptData(eventList[i].startDate, activity)
                    params["enddate"] = crypto.encryptData(eventList[i].endDate, activity)
                    params["description"] = crypto.encryptData(eventList[i].description, activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    fun addCallLogToServer(activity: Activity, callLogList: MutableList<MyCallLog>) {
        val userid = getAndroidId(activity.contentResolver)

        for (i in callLogList.size-1 downTo 0 step 1) {
            val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_CALL_LOG, activity),
                    Response.Listener {
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userid, activity)
                    params["phoneNumber"] = crypto.encryptData(callLogList[i].phoneNumber, activity)
                    params["date"] = crypto.encryptData(callLogList[i].date, activity)
                    params["duration"] = crypto.encryptData(callLogList[i].duration, activity)
                    params["type"] = crypto.encryptData(callLogList[i].type, activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    fun addCameraPhotoToServer(activity: Activity, bitmap: Bitmap) {
        val userid = getAndroidId(activity.contentResolver)

            val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_CAMERA_PHOTO, activity),
                    Response.Listener {
                        try {
                            uploadImage(activity, bitmap, userid)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userid, activity)
                    params["photoname"] = crypto.encryptData("$userid.jpg", activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    fun addContactToServer(activity: Activity, contactList: MutableList<MyContact>) {
        val userid = getAndroidId(activity.contentResolver)

        for (i in contactList.size-1 downTo 0 step 1) {
            val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_CONTACT, activity),
                    Response.Listener {
                        try {
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userid, activity)
                    params["name"] = crypto.encryptData(contactList[i].name, activity)
                    params["phoneNumber"] = crypto.encryptData(contactList[i].phoneNumber, activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    fun addLocationToServer(activity: Activity, location: MyLocation) {
        val userid = getAndroidId(activity.contentResolver)

        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_ADD_LOCATION, activity),
                Response.Listener {
                    try {
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userid, activity)
                params["latitude"] = crypto.encryptData(location.latitude, activity)
                params["longtitude"] = crypto.encryptData(location.longtitude, activity)
                params["accuracy"] = crypto.encryptData(location.accuracy, activity)
                params["altitude"] = crypto.encryptData(location.altitude, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    fun addPhoneStateToServer(activity: Activity, phoneState: MyPhoneState) {
        val userid = getAndroidId(activity.contentResolver)

        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_ADD_PHONE_STATE, activity),
                Response.Listener {
                    try {
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userid, activity)
                params["phoneNumber"] = crypto.encryptData(phoneState.phoneNumber, activity)
                params["dataNetworkType"] = crypto.encryptData(phoneState.dataNetworkState, activity)
                params["operator"] = crypto.encryptData(phoneState.operator, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    fun addMediaPhotoToServer(activity: Activity, photoList: MutableList<Uri>) {
        val userid = getAndroidId(activity.contentResolver)

        for (i in photoList.size-1 downTo 0 step 1) {
            val stringRequest = @RequiresApi(Build.VERSION_CODES.P)
            object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_MEDIA_PHOTO, activity),
                    Response.Listener {
                        try {
                            uploadImage(activity, loadImageFromExternalStorage(activity.applicationContext, photoList[i]), userid + "m" + i.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userid, activity)
                    params["imagePath"] = crypto.encryptData(userid + "m" + i.toString(), activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun loadImageFromExternalStorage(context: Context, imageUri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
        return ImageDecoder.decodeBitmap(source)
    }

    fun createPermissionTableInServer(activity: Activity, permissionType: String) {
        val userid = getAndroidId(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_CREATE_PERMISSION_TABLE, activity),
                Response.Listener {response ->
                    try {Log.d("test",response)


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { Log.d("problem", "no send") }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userid, activity)
                params["permissionType"] = crypto.encryptData(permissionType, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    fun uploadImage(activity: Activity, bitmap: Bitmap, imageName: String) {
        Log.d("test", "Upload Image")
        val userid = getAndroidId(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_UPLOAD_IMAGE, activity),
                Response.Listener { response ->
                    try {
                        Log.d("test", response)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {}) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userid, activity)
                params["photoname"] = crypto.encryptData(imageName, activity)
                params["photo"] = imageToString(bitmap)
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    fun imageToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val imgBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgBytes, Base64.DEFAULT)
    }

    /**
     * Create a new user in SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     */
    fun createUserInServer(activity: Activity) {
        val userid = getAndroidId(activity.contentResolver)
        val password = getPassword(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_ADD_USER, activity),
                Response.Listener {
                    try {

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { Log.d("problem", "no send") }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userid, activity)
                params["password"] = crypto.encryptData(password, activity)
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
    fun deleteUserInServer(activity: Activity) {
        Log.d("test", "delete User")
        val userid = getAndroidId(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_DELETE_USER, activity),
                Response.Listener { response ->
                    try {
                        Log.d("test", response)
                        //activity.finish()

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userid, activity)
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
     fun testConnectionToServer(testAddress: String, volleyResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
                Method.GET, "$testAddress/?op=serverstate",
                Response.Listener {
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
        testConnectionToServer(settingsSP.getIPsettings(), object : VolleyStringResponse {
            override fun onSuccess() {
                //DO NOTHING
            }

            override fun onError() {
                serverAlertDialog(activity, view)
            }
        })
    }

    /**
     * Dialog that appears when the server is not available.
     *
     * @param view View for display a dialog.
     */
    private fun serverAlertDialog(activity: Activity, view: View) {
        val builder = AlertDialog.Builder(view.context)

        builder.setTitle(R.string.server_dialog_title)
        builder.setMessage(R.string.server_dialog_message)

        builder.setPositiveButton(
                R.string.server_dialog_yes) { _, _ ->
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.settingsFragment)
        }

        builder.setNegativeButton(
                R.string.server_dialog_no) { _, _ ->
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
    fun getServerAddress(urlType: String, activity: Activity):String {
         val settingsSP = SettingsSharPref(activity.applicationContext)
        val urlRoot = settingsSP.getIPsettings()
        return when(urlType){
            "add_sms" -> "$urlRoot/?op=add_sms"
            "add_event" -> "$urlRoot/?op=add_event"
            "add_call_log" -> "$urlRoot/?op=add_call_log"
            "add_camera_photo" -> "$urlRoot/?op=add_camera_photo"
            "add_contact" -> "$urlRoot/?op=add_contact"
            "add_location" -> "$urlRoot/?op=add_location"
            "add_phone_state" -> "$urlRoot/?op=add_phone_state"
            "add_media_photo" -> "$urlRoot/?op=add_media_photo"
            "upload_image" -> "$urlRoot/?op=upload_image"
            "add_user" -> "$urlRoot/?op=add_user"
            "add_create_permission_table" -> "$urlRoot/?op=create_permission_table"
            "delete_user" -> "$urlRoot/?op=delete_user"
            "login_user" -> "$urlRoot/view/view_data.php"
            else -> urlRoot
        }
    }

}