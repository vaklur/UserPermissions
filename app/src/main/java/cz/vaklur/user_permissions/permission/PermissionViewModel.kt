package cz.vaklur.user_permissions.permission

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.permission.permission_types.calendar_permission.CalendarFunction
import cz.vaklur.user_permissions.permission.permission_types.call_log_permission.CallLogFunction
import cz.vaklur.user_permissions.permission.permission_types.contact_permission.ContactFunction
import cz.vaklur.user_permissions.permission.permission_types.location_permission.LocationFunction
import cz.vaklur.user_permissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import cz.vaklur.user_permissions.permission.permission_types.sms_permission.SmsFunction
import cz.vaklur.user_permissions.permission.permission_types.storage_permission.StorageFunction
import cz.vaklur.user_permissions.settings.SettingsSharedPreferences
import cz.vaklur.user_permissions.volley_communication.CommunicationService

/**
 * View model for managing permission fragments data.
 */
class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    data class PermissionVMInit(
        var serverAddress: String,
        val theoryText: String,
        val permissionType: String,
        val permissionText: String
    )

    private var _successServerCommunication = MutableLiveData<String>()
    val successServerCommunication: LiveData<String> = _successServerCommunication

    private val ok = "ok"
    private val waiting = "waiting"
    private val error = "error"


    // ID of permission
    private var permissionId: Int

    // Control if data was send to server
    private var dataIsSend: Boolean

    // save photo from camera for offline example
    private var photo: Bitmap? = null

    // User created in server
    private var userCreatedInServer: Boolean

    private val comFun: CommunicationService
    private val permissionFun: PermissionFunction

    private var theoryText: String
    private var permissionType: String
    private var permissionText: String

    var userId: String
    var userPassword: String


    init {
        permissionId = 0
        dataIsSend = false
        userCreatedInServer = false
        comFun = CommunicationService(application)
        userId = comFun.userId
        userPassword = comFun.password
        permissionFun = PermissionFunction()
        theoryText = ""
        permissionType = ""
        permissionText = ""
    }

    // Getter and Setters
    fun savePhoto(newPhoto: Bitmap) {
        photo = newPhoto
    }

    fun getPhoto(): Bitmap? {
        return photo
    }

    fun setPermissionID(newPermissionId: Int) {
        permissionId = newPermissionId
    }

    fun getPermissionID(): Int {
        return permissionId
    }

    fun saveDataIsSend(newDataIsSend: Boolean) {
        dataIsSend = newDataIsSend
    }

    fun getDataIsSend(): Boolean {
        return dataIsSend
    }

    /**
     * Initialize variables in permission theory fragment based on permission ID.
     *
     * @param context Application context.
     */
    fun initPermissionTexts(context: Context): PermissionVMInit {
        when (permissionId) {
            1 -> {
                permissionType = Manifest.permission.READ_SMS
                permissionText = context.getString(R.string.sms)
                theoryText = context.resources.getString(R.string.sms_theory)
            }
            2 -> {
                permissionType = Manifest.permission.READ_CONTACTS
                permissionText = context.getString(R.string.contacts)
                theoryText = context.resources.getString(R.string.contact_theory)
            }
            3 -> {
                permissionType = Manifest.permission.READ_CALL_LOG
                permissionText = context.getString(R.string.call_log)
                theoryText = context.resources.getString(R.string.call_log_theory)
            }
            4 -> {
                permissionType = Manifest.permission.READ_CALENDAR
                permissionText = context.getString(R.string.calendar)
                theoryText = context.resources.getString(R.string.calendar_theory)
            }
            5 -> {
                permissionType = Manifest.permission.ACCESS_FINE_LOCATION
                permissionText = context.getString(R.string.location)
                theoryText = context.resources.getString(R.string.location_theory)
            }
            6 -> {
                permissionType = Manifest.permission.READ_EXTERNAL_STORAGE
                permissionText = context.getString(R.string.storage)
                theoryText = context.resources.getString(R.string.storage_theory)
            }
            7 -> {
                permissionType = Manifest.permission.READ_PHONE_STATE
                permissionText = context.getString(R.string.phone)
                theoryText = context.resources.getString(R.string.phone_theory)
            }
            8 -> {
                permissionType = Manifest.permission.CAMERA
                permissionText = context.getString(R.string.camera)
                theoryText = context.resources.getString(R.string.camera_theory)
            }
        }
        return PermissionVMInit(
            SettingsSharedPreferences(context).getIpSettings(),
            theoryText,
            permissionType,
            permissionText
        )
    }

    /**
     * Function which send permission data to server
     *
     * @param activity Fragment activity.
     * @param context Application context.
     */
    fun sendDataToServer(activity: Activity, context: Context) {
        _successServerCommunication.value = waiting
        Log.d("test", "sendDataToServer")
        Log.d("test", "dataIsSend$dataIsSend")
        comFun.testConnectionToServer(SettingsSharedPreferences(context).getIpSettings(),
            object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    Log.d("test", "dataIsSend$dataIsSend")
                    if (!dataIsSend) {
                        createUserInServer(activity, context)
                        Log.d("test", "Go To createUserInServer")
                    } else {
                        _successServerCommunication.value = ok
                    }
                }

                override fun onError() {
                    _successServerCommunication.value = error

                }
            })
    }

    private fun createUserInServer(activity: Activity, context: Context) {
        Log.d("test", "createUserInServer")
        if (userCreatedInServer) {
            createPermissionTableInServer(activity, context)
        } else {
            comFun.createUserInServer(
                object : CommunicationService.VolleyStringResponse {
                    override fun onSuccess() {
                        createPermissionTableInServer(activity, context)
                        userCreatedInServer = true
                    }

                    override fun onError() {
                        _successServerCommunication.value = error
                    }
                })
        }
    }

    private fun createPermissionTableInServer(activity: Activity, context: Context) {
        Log.d("test", "createPermissionTableInServer")
        comFun.createPermissionTableInServer(
            permissionFun.getPermissionTypeFromPermissionID(
                permissionId
            ), object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    sendPermissionDataToServer(activity, context)
                }

                override fun onError() {
                    _successServerCommunication.value = error
                }

            })
    }

    /**
     * Send permission data to server based on permission ID.
     */
    private fun sendPermissionDataToServer(activity: Activity, context: Context) {
        Log.d("test", "sendPermissionDataToServer")
        dataIsSend = true
        when (permissionId) {
            1 -> comFun.addSMStoServer(SmsFunction().readSms(10, activity.contentResolver, context))
            2 -> comFun.addContactToServer(
                ContactFunction().readContacts(
                    activity.contentResolver,
                    10
                )
            )
            3 -> comFun.addCallLogToServer(
                CallLogFunction().readCallLogs(
                    10,
                    activity.contentResolver,
                    context
                )
            )
            4 -> comFun.addEventToServer(
                CalendarFunction().readCalendarEvents(
                    activity.contentResolver,
                    10
                )
            )
            5 -> LocationFunction().getLastLocation(activity, context)
            6 -> comFun.addMediaPhotoToServer(
                activity,
                StorageFunction().getPhotosFromGallery(activity.contentResolver, 10)
            )
            7 -> comFun.addPhoneStateToServer(PhoneStateFunction().getDataFromSIM(context))
            8 -> dataIsSend = false
        }
        _successServerCommunication.value = ok

    }

    fun sendPermissionDataToServer(cameraPhoto: Bitmap) {
        Log.d("test", "sendPermissionDataToServer")
        comFun.addCameraPhotoToServer(cameraPhoto)
        _successServerCommunication.value = ok
        dataIsSend = true
    }

    /**
     * Function which delete all user data in server.
     */
    fun deleteUserInServer() {
        comFun.deleteUserInServer()
        userCreatedInServer = false
    }

    /**
     * Function which delete user table in server and clear a View Model permission variables to default
     */
    fun deleteUserTableInServer() {
        comFun.deleteUserTableInServer(permissionFun.getPermissionTypeFromPermissionID(permissionId))
        permissionId = 0
        dataIsSend = false
        photo = null
    }


}

