package cz.vaklur.user_permissions.server_communication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
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
import cz.vaklur.user_permissions.Cryptography
import cz.vaklur.user_permissions.constants.EndPoints
import cz.vaklur.user_permissions.permission.permission_types.calendar_permission.MyCalendarEvent
import cz.vaklur.user_permissions.permission.permission_types.call_log_permission.MyCallLog
import cz.vaklur.user_permissions.permission.permission_types.contact_permission.MyContact
import cz.vaklur.user_permissions.permission.permission_types.location_permission.MyLocation
import cz.vaklur.user_permissions.permission.permission_types.phone_state_permission.MyPhoneState
import cz.vaklur.user_permissions.permission.permission_types.sms_permission.MySms
import cz.vaklur.user_permissions.permission.permission_types.storage_permission.MyStorage
import cz.vaklur.user_permissions.settings.SettingsSharedPreferences
import org.json.JSONException
import java.io.ByteArrayOutputStream


/**
 * Functions for communication with server.
 */
class CommunicationService(application: Application) {
    val userId = getAndroidId(application.contentResolver)
    val password = getPassword(application.contentResolver)

    private val settingsSP = SettingsSharedPreferences(application.applicationContext)

    private val pubKeyFile =
        application.applicationContext.assets.open("public_key.pem").bufferedReader()
            .use { it.readText() }
    private val crypto = Cryptography(pubKeyFile)

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
    private fun getAndroidId(contentResolver: ContentResolver): String {
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
    private fun getPassword(contentResolver: ContentResolver): String {
        var androidPassword = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        androidPassword = androidPassword.substring(7, 12)
        return androidPassword
    }

    /**
     * Add a sms to SQL database on server.
     *
     * @param smsList Sms to send to server.
     */
    fun addSMStoServer(smsList: MutableList<MySms>, volleyResponse: VolleyStringResponse) {
        if (smsList.isNotEmpty()) {
            for (i in smsList.size - 1 downTo 0 step 1) {
                val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_SMS),
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
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = crypto.encryptData(userId)
                        params["date"] = crypto.encryptData(smsList[i].date)
                        params["number"] = crypto.encryptData(smsList[i].number)
                        params["text"] = crypto.encryptData(smsList[i].text)
                        params["type"] = crypto.encryptData(smsList[i].type)
                        return params
                    }
                }
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        } else volleyResponse.onSuccess()
    }

    /**
     * Add a calendar event to SQL database on server.
     *
     * @param eventList Calendar events to send to server.
     */
    fun addEventToServer(
        eventList: MutableList<MyCalendarEvent>,
        volleyResponse: VolleyStringResponse
    ) {
        if (eventList.isNotEmpty()) {
            for (i in eventList.size - 1 downTo 0 step 1) {
                val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_EVENT),
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
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = crypto.encryptData(userId)
                        params["title"] = crypto.encryptData(eventList[i].title)
                        params["startdate"] = crypto.encryptData(eventList[i].startDate)
                        params["enddate"] = crypto.encryptData(eventList[i].endDate)
                        params["description"] = crypto.encryptData(eventList[i].description)
                        return params
                    }
                }
                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        } else volleyResponse.onSuccess()
    }

    /**
     * Add a call logs to SQL database on server.
     *
     * @param callLogList Call logs to send to server.
     */
    fun addCallLogToServer(
        callLogList: MutableList<MyCallLog>,
        volleyResponse: VolleyStringResponse
    ) {
        if (callLogList.isNotEmpty()) {
            for (i in callLogList.size - 1 downTo 0 step 1) {
                val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_CALL_LOG),
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
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = crypto.encryptData(userId)
                        params["phoneNumber"] = crypto.encryptData(callLogList[i].phoneNumber)
                        params["date"] = crypto.encryptData(callLogList[i].date)
                        params["duration"] = crypto.encryptData(callLogList[i].duration)
                        params["type"] = crypto.encryptData(callLogList[i].type)
                        return params
                    }
                }

                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        } else volleyResponse.onSuccess()
    }

    /**
     * Add a photo from camera to SQL database on server.
     *
     * @param bitmap Photo from camera to send to server.
     */
    fun addCameraPhotoToServer(bitmap: Bitmap?, volleyResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_ADD_CAMERA_PHOTO),
            Response.Listener {
                try {
                    if (bitmap != null) {
                        uploadImage(bitmap, userId, volleyResponse)
                    } else volleyResponse.onSuccess()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },

            Response.ErrorListener {
                volleyResponse.onError()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
                params["photoname"] = crypto.encryptData("$userId.jpg")
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    /**
     * Add a contacts to SQL database on server.
     *
     * @param contactList Contacts to send to server.
     */
    fun addContactToServer(
        contactList: MutableList<MyContact>,
        volleyResponse: VolleyStringResponse
    ) {
        if (contactList.isNotEmpty()) {
            for (i in contactList.size - 1 downTo 0 step 1) {
                val stringRequest = object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_CONTACT),
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
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = crypto.encryptData(userId)
                        params["name"] = crypto.encryptData(contactList[i].name)
                        params["phoneNumber"] = crypto.encryptData(contactList[i].phoneNumber)
                        return params
                    }
                }

                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        } else volleyResponse.onSuccess()
    }

    /**
     * Add a last known location to SQL database on server.
     *

     * @param location Last know location to send to server.
     */
    fun addLocationToServer(location: MyLocation, volleyResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_ADD_LOCATION),
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
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
                params["latitude"] = crypto.encryptData(location.latitude)
                params["longtitude"] = crypto.encryptData(location.longitude)
                params["accuracy"] = crypto.encryptData(location.accuracy)
                params["altitude"] = crypto.encryptData(location.altitude)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)

    }

    /**
     * Add a information from SIM to SQL database on server.
     *
     * @param phoneState SIM information to send to server.
     */
    fun addPhoneStateToServer(phoneState: MyPhoneState, volleyResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_ADD_PHONE_STATE),
            Response.Listener {
                try {
                    volleyResponse.onSuccess()
                } catch (e: JSONException) {
                }
            },
            Response.ErrorListener {
                volleyResponse.onError()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
                params["phoneNumber"] = crypto.encryptData(phoneState.phoneNumber)
                params["dataNetworkType"] = crypto.encryptData(phoneState.dataNetworkState)
                params["operator"] = crypto.encryptData(phoneState.operator)
                return params
            }
        }
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Add a images from external storage to SQL database on server.
     *
     * @param photoList Images to send to server.
     */
    fun addMediaPhotoToServer(
        activity: Activity,
        photoList: MutableList<MyStorage>,
        volleyResponse: VolleyStringResponse
    ) {
        if (photoList.isNotEmpty()) {
            for (i in photoList.size - 1 downTo 0 step 1) {
                val stringRequest = @RequiresApi(Build.VERSION_CODES.P)
                object : StringRequest(
                    Method.POST, getServerAddress(EndPoints.URL_ADD_MEDIA_PHOTO),
                    Response.Listener {
                        try {
                            uploadImage(
                                loadImageFromExternalStorage(
                                    activity.applicationContext,
                                    photoList[i].uri
                                ), userId + "m" + i.toString(), volleyResponse
                            )
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },

                    Response.ErrorListener {
                        volleyResponse.onError()
                    }) {
                    @Throws(AuthFailureError::class)
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["userid"] = crypto.encryptData(userId)
                        params["imagePath"] = crypto.encryptData(userId + "m" + i.toString())
                        return params
                    }
                }

                VolleySingleton.instance?.addToRequestQueue(stringRequest)
            }
        } else volleyResponse.onSuccess()
    }

    /**
     * Function which load images from external storage.
     *
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
     * Add the permission type to user table in SQL database on server.
     *
     * @param permissionType The type of abuse example selected.
     */
    fun addPermissionTypeToServer(
        permissionType: String,
        volleyResponse: VolleyStringResponse
    ) {

        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_ADD_PERMISSION_TYPE),
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
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
                params["permissionType"] = crypto.encryptData(permissionType)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Function for upload image to server.
     *
     * @param bitmap Image on bitmap format.
     * @param imageName Name of the image.
     */
    fun uploadImage(bitmap: Bitmap, imageName: String, volleyStringResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_UPLOAD_IMAGE),
            Response.Listener {
                try {
                    volleyStringResponse.onSuccess()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {
                volleyStringResponse.onError()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
                params["photoname"] = crypto.encryptData(imageName)
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
     */
    fun createUserInServer(volleyResponse: VolleyStringResponse) {
        Log.d("test","CM Create user in server")
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_ADD_USER),
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
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
                params["password"] = crypto.encryptData(password)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Delete user permission type in SQL database on server.
     *
     * @param table Name of permission table.

     */
    fun deletePermissionTypeInServer(table: String) {
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_DELETE_PERMISSION_TYPE),
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
                params["userid"] = crypto.encryptData(userId)
                params["permissionType"] = crypto.encryptData(table)
                return params
            }
        }

        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }

    /**
     * Delete user in SQL database on server.
     *
     */
    fun deleteUserInServer(volleyResponse: VolleyStringResponse) {
        val stringRequest = object : StringRequest(
            Method.POST, getServerAddress(EndPoints.URL_DELETE_USER),
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
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = crypto.encryptData(userId)
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
     * @return Whole url for command.
     */
    fun getServerAddress(urlType: String): String {
        val urlRoot = settingsSP.getIpSettings()
        return when (urlType) {
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
            "add_permission_type" -> "$urlRoot/?op=add_permission_type"
            "delete_user" -> "$urlRoot/?op=delete_user"
            "delete_permission_type" -> "$urlRoot/?op=delete_permission_type"
            "login_user" -> "$urlRoot/view/view_data.php"
            else -> urlRoot
        }
    }
}