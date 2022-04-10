package cz.vaklur.user_permissions.permission.view_model

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
import cz.vaklur.user_permissions.permission.PermissionFunction
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

    private lateinit var theoryText: String
    private lateinit var permissionType: String
    private lateinit var permissionText: String

    // Live data - server communication
    private var _successServerCommunication = MutableLiveData<String>()
    val successServerCommunication: LiveData<String> = _successServerCommunication

    // VolleyStringResponse for managing communication states
    private val volleyAddDataToServerResponse = object : CommunicationService.VolleyStringResponse {
        override fun onSuccess() {
            dataIsSent = true
            _successServerCommunication.value = ok
        }

        override fun onError() {
            _successServerCommunication.value = error
        }
    }

    private val ok = "ok"
    private val waiting = "waiting"
    private val error = "error"

    // ID of permission
    private var permissionId: Int

    // Control if data was send to server
    private var dataIsSent: Boolean

    // save photo from camera for offline example
    private var photo: Bitmap? = null

    // User created in server
    private var userCreatedInServer: Boolean

    private val communicationService: CommunicationService
    private val permissionFunction: PermissionFunction
    private val sharedPreferences: SettingsSharedPreferences

    /**
     * Initialize ViewModel with necessary variables.
     */
    init {
        permissionId = 0
        dataIsSent = false
        userCreatedInServer = false
        communicationService = CommunicationService(application)
        permissionFunction = PermissionFunction()
        sharedPreferences = SettingsSharedPreferences(application.applicationContext)
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
        dataIsSent = newDataIsSend
    }

    fun getDataIsSend(): Boolean {
        return dataIsSent
    }

    /**
     * Initialize variables in permission theory fragment based on permission ID.
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
     * Start the send data flow to server  for permission data.
     */
    fun sendDataToServer(activity: Activity, context: Context) {
        _successServerCommunication.value = waiting
        communicationService.testConnectionToServer(SettingsSharedPreferences(context).getIpSettings(),
            object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    if (!dataIsSent) {
                        createUserInServer(activity, context)
                    } else {
                        _successServerCommunication.value = ok
                    }
                }

                override fun onError() {
                    _successServerCommunication.value = error
                }
            })
    }

    /**
     * Create user account in server.
     */
    private fun createUserInServer(activity: Activity, context: Context) {
        if (userCreatedInServer) {
            createPermissionTableInServer(activity, context)
        } else {
            communicationService.createUserInServer(
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

    /**
     * Create table for user permission data in server.
     */
    private fun createPermissionTableInServer(activity: Activity, context: Context) {
        communicationService.createPermissionTableInServer(
            permissionFunction.getPermissionTypeFromPermissionID(
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
        when (permissionId) {
            1 -> communicationService.addSMStoServer(
                SmsFunction().readSms(
                    10,
                    activity.contentResolver,
                    context
                ), volleyAddDataToServerResponse
            )
            2 -> communicationService.addContactToServer(
                ContactFunction().readContacts(
                    activity.contentResolver,
                    10
                ), volleyAddDataToServerResponse
            )
            3 -> communicationService.addCallLogToServer(
                CallLogFunction().readCallLogs(
                    10,
                    activity.contentResolver,
                    context
                ), volleyAddDataToServerResponse
            )
            4 -> communicationService.addEventToServer(
                CalendarFunction().readCalendarEvents(
                    activity.contentResolver,
                    10
                ), volleyAddDataToServerResponse
            )
            5 -> LocationFunction().getLastLocation(
                activity,
                context,
                volleyAddDataToServerResponse
            )
            6 -> communicationService.addMediaPhotoToServer(
                activity,
                StorageFunction().getPhotosFromGallery(activity.contentResolver, 10),
                volleyAddDataToServerResponse
            )
            7 -> communicationService.addPhoneStateToServer(
                PhoneStateFunction().getDataFromSIM(
                    context
                ), volleyAddDataToServerResponse
            )
            8 -> {
                dataIsSent = false
                _successServerCommunication.value = ok
            }
        }
    }

    /**
     * Send permission data to server - only for camera permission.
     */
    fun sendPermissionDataToServer(cameraPhoto: Bitmap) {
        Log.d("test", "sendPermissionDataToServer")
        communicationService.addCameraPhotoToServer(cameraPhoto, volleyAddDataToServerResponse)
        _successServerCommunication.value = ok
        dataIsSent = true
    }

    /**
     * Delete user account and all users data in server.
     */
    fun deleteUserInServer() {
        communicationService.deleteUserInServer()
        userCreatedInServer = false
    }

    /**
     * Delete table for user permission data in server.
     */
    fun deleteUserTableInServer() {
        communicationService.deleteUserTableInServer(
            permissionFunction.getPermissionTypeFromPermissionID(
                permissionId
            )
        )
        dataIsSent = false
        photo = null
    }


}

