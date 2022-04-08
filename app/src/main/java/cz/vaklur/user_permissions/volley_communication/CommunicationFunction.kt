package cz.vaklur.user_permissions.volley_communication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import cz.vaklur.user_permissions.EndPoints
import cz.vaklur.user_permissions.permission.permission_types.calendar_permission.MyCalendarEvent
import cz.vaklur.user_permissions.permission.permission_types.call_log_permission.MyCallLog
import cz.vaklur.user_permissions.permission.permission_types.contact_permission.MyContact
import cz.vaklur.user_permissions.permission.permission_types.location_permission.MyLocation
import cz.vaklur.user_permissions.permission.permission_types.phone_state_permission.MyPhoneState
import cz.vaklur.user_permissions.permission.permission_types.sms_permission.MySms
import cz.vaklur.user_permissions.permission.permission_types.storage_permission.MyStorage
import cz.vaklur.user_permissions.settings.SettingsSharPref
import org.json.JSONException
import java.io.ByteArrayOutputStream


/**
 * Functions for communication with server.
 */
class CommunicationFunction {

    private val crypto = cz.vaklur.user_permissions.Cryptography()

    /**
     * Interface for test connection to server.
     */
    interface VolleyStringResponse {
        fun onSuccess()
        fun onError()
    }

    /**
     * Generate a ID for user from device ID
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

    /**
     * Generate a password for user from device ID
     *
     * @param contentResolver Provides applications access to the content model.
     * @return User password.
     */
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
         val userID = getAndroidId(activity.contentResolver)

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
                     params["userid"] = crypto.encryptData(userID, activity)
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

    /**
     * Add a calendar event to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param eventList Calendar events to send to server.
     */
    fun addEventToServer(activity: Activity, eventList: MutableList<MyCalendarEvent>) {
        val userID = getAndroidId(activity.contentResolver)

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
                    params["userid"] = crypto.encryptData(userID, activity)
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

    /**
     * Add a call logs to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param callLogList Call logs to send to server.
     */
    fun addCallLogToServer(activity: Activity, callLogList: MutableList<MyCallLog>) {
        val userID = getAndroidId(activity.contentResolver)

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
                    params["userid"] = crypto.encryptData(userID, activity)
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

    /**
     * Add a photo from camera to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param bitmap Photo from camera to send to server.
     */
    fun addCameraPhotoToServer(activity: Activity, bitmap: Bitmap) {
        val userID = getAndroidId(activity.contentResolver)

            val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_CAMERA_PHOTO, activity),
                    Response.Listener {
                        try {
                            uploadImage(activity, bitmap, userID)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userID, activity)
                    params["photoname"] = crypto.encryptData("$userID.jpg", activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    /**
     * Add a contacts to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param contactList Contacts to send to server.
     */
    fun addContactToServer(activity: Activity, contactList: MutableList<MyContact>) {
        val userID = getAndroidId(activity.contentResolver)

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
                    params["userid"] = crypto.encryptData(userID, activity)
                    params["name"] = crypto.encryptData(contactList[i].name, activity)
                    params["phoneNumber"] = crypto.encryptData(contactList[i].phoneNumber, activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    /**
     * Add a last known location to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param location Last know location to send to server.
     */
    fun addLocationToServer(activity: Activity, location: MyLocation) {
        val userID = getAndroidId(activity.contentResolver)

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
                params["userid"] = crypto.encryptData(userID, activity)
                params["latitude"] = crypto.encryptData(location.latitude, activity)
                params["longtitude"] = crypto.encryptData(location.longitude, activity)
                params["accuracy"] = crypto.encryptData(location.accuracy, activity)
                params["altitude"] = crypto.encryptData(location.altitude, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    /**
     * Add a information from SIM to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param phoneState SIM information to send to server.
     */
    fun addPhoneStateToServer(activity: Activity, phoneState: MyPhoneState) {
        val userID = getAndroidId(activity.contentResolver)

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
                params["userid"] = crypto.encryptData(userID, activity)
                params["phoneNumber"] = crypto.encryptData(phoneState.phoneNumber, activity)
                params["dataNetworkType"] = crypto.encryptData(phoneState.dataNetworkState, activity)
                params["operator"] = crypto.encryptData(phoneState.operator, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    /**
     * Add a images from external storage to SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param photoList Images to send to server.
     */
    fun addMediaPhotoToServer(activity: Activity, photoList: MutableList<MyStorage>) {
        val userID = getAndroidId(activity.contentResolver)

        for (i in photoList.size-1 downTo 0 step 1) {
            val stringRequest = @RequiresApi(Build.VERSION_CODES.P)
            object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_MEDIA_PHOTO, activity),
                    Response.Listener {
                        try {
                            uploadImage(activity, loadImageFromExternalStorage(activity.applicationContext, photoList[i].uri), userID + "m" + i.toString())
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener { }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["userid"] = crypto.encryptData(userID, activity)
                    params["imagePath"] = crypto.encryptData(userID + "m" + i.toString(), activity)
                    return params
                }
            }

            VolleySingleton.instance?.addToRequestQueue(stringRequest)
        }
    }

    /**
     * Function which load images from external storage.
     *
     * @param context Activity contexts.
     * @param imageUri Uri of image.
     *
     * @return Image in bitmap format.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun loadImageFromExternalStorage(context: Context, imageUri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
        return ImageDecoder.decodeBitmap(source)
    }

    /**
     * Create a table for data from abuse permission example in SQL database on server.
     *
     * @param activity Activity for get [ContentResolver].
     * @param permissionType The type of abuse example selected.
     */
    fun createPermissionTableInServer(activity: Activity, permissionType: String) {
        val userID = getAndroidId(activity.contentResolver)
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
                params["userid"] = crypto.encryptData(userID, activity)
                params["permissionType"] = crypto.encryptData(permissionType, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Function for upload image to server.
     *
     * @param activity activity Activity for get [ContentResolver].
     * @param bitmap Image on bitmap format.
     * @param imageName Name of the image.
     */
    fun uploadImage(activity: Activity, bitmap: Bitmap, imageName: String) {
        Log.d("test", "Upload Image")
        val userID = getAndroidId(activity.contentResolver)
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
                params["userid"] = crypto.encryptData(userID, activity)
                params["photoname"] = crypto.encryptData(imageName, activity)
                params["photo"] = imageToString(bitmap)
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Function for convert image in bitmap format to string (Compress bitmap to 10% of original quality).
     *
     * @param bitmap Image in bitmap format.
     *
     * @return Image in string format.
     */
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
    fun createUserInServer(activity: Activity,volleyResponse: VolleyStringResponse) {
        val userID = getAndroidId(activity.contentResolver)
        val password = getPassword(activity.contentResolver)
        val stringRequest = object : StringRequest(
                Method.POST, getServerAddress(EndPoints.URL_ADD_USER, activity),
                Response.Listener {response ->
                    try {Log.d("test",response)
                        volleyResponse.onSuccess()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },

                Response.ErrorListener { Log.d("problem", "no send") }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userID, activity)
                params["password"] = crypto.encryptData(password, activity)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Delete user permission table in SQL database on server.
     *
     * @param table Name of permission table.
     * @param activity Activity for get [ContentResolver].
     */
    fun deleteUserTableInServer(table:String,activity: Activity) {
        Log.d("test", "delete User table")
        val userID = getAndroidId(activity.contentResolver)
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_DELETE_USER_TABLE, activity),
            Response.Listener { response ->
                try {
                    Log.d("test", response)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },

            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userID, activity)
                params["tablename"] = crypto.encryptData(table, activity)
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
        val userID = getAndroidId(activity.contentResolver)
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
                params["userid"] = crypto.encryptData(userID, activity)
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
     * Get the whole url command server address.
     *
     * @param urlType Type of the server command.
     * @param activity Activity for access to [SettingsSharPref].
     * @return Whole url for command.
     */
    fun getServerAddress(urlType: String, activity: Activity):String {
         val settingsSP = SettingsSharPref(activity.applicationContext)
        val urlRoot = settingsSP.getIpSettings()
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
            "delete_user_table" -> "$urlRoot/?op=delete_user_table"
            "login_user" -> "$urlRoot/view/view_data.php"
            else -> urlRoot
        }
    }
}