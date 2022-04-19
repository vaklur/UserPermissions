package cz.vaklur.user_permissions.permission.view_model

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cz.vaklur.user_permissions.R
import cz.vaklur.user_permissions.constants.Constants
import cz.vaklur.user_permissions.permission.PermissionFunction
import cz.vaklur.user_permissions.permission.permission_types.calendar_permission.CalendarFunction
import cz.vaklur.user_permissions.permission.permission_types.call_log_permission.CallLogFunction
import cz.vaklur.user_permissions.permission.permission_types.contact_permission.ContactFunction
import cz.vaklur.user_permissions.permission.permission_types.location_permission.LocationFunction
import cz.vaklur.user_permissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import cz.vaklur.user_permissions.permission.permission_types.sms_permission.SmsFunction
import cz.vaklur.user_permissions.permission.permission_types.storage_permission.StorageFunction
import cz.vaklur.user_permissions.server_communication.CommunicationService
import cz.vaklur.user_permissions.settings.SettingsSharedPreferences

/**
 * View model for managing permission fragments data.
 */
class PermissionViewModel(application: Application) : AndroidViewModel(application) {

    // Sms theory initialize strings
    data class PermissionVMInit(
        var serverAddress: String,
        val theoryText: String,
        val permissionType: String,
        val permissionText: String
    )

    // Live data - server communication
    private var _successServerCommunication = MutableLiveData<String>()
    val successServerCommunication: LiveData<String> = _successServerCommunication

    //Live data - progress bar
    private var _exampleButtonEnable = MutableLiveData<Boolean>()
    val exampleButtonEnable: LiveData<Boolean> = _exampleButtonEnable

    private var _progressBarVisibility = MutableLiveData<Int>()
    var progressBarVisibility: LiveData<Int> = _progressBarVisibility

    private var _progressBarTextViewVisibility = MutableLiveData<Int>()
    var progressBarTextViewVisibility: LiveData<Int> = _progressBarTextViewVisibility

    // VolleyStringResponse for managing communication states
    private val volleyAddDataToServerResponse = object : CommunicationService.VolleyStringResponse {
        override fun onSuccess() {
            dataIsSent = true
            _successServerCommunication.value = ok
            progressBarOn(false)
        }

        override fun onError() {
            _successServerCommunication.value = error
            progressBarOn(false)
        }
    }

    // States of server communication
    private val ok = Constants.STATE_OK
    private val waiting = Constants.STATE_WAITING
    private val error = Constants.STATE_ERROR

    // ID of permission
    private var permissionId: Int

    // Control if data was send to server
    private var dataIsSent: Boolean

    // save photo from camera for offline example
    private var photo: Bitmap? = null

    // User created in server
    private var userCreatedInServer: Boolean

    // Variables for functions
    private val communicationService: CommunicationService
    private val permissionFunction: PermissionFunction
    private val sharedPreferences: SettingsSharedPreferences

    /**
     * Initialize ViewModel with necessary variables.
     */
    init {
        permissionId = 0
        dataIsSent = false
        communicationService = CommunicationService(application)
        permissionFunction = PermissionFunction()
        sharedPreferences = SettingsSharedPreferences(application.applicationContext)
        userCreatedInServer = sharedPreferences.getUserCreatedState()
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
     * Function set the visibility of "connecting to server" progress bar
     */
    private fun progressBarOn(visible: Boolean) {
        var visibility = View.GONE
        if (visible) visibility = View.VISIBLE
        _progressBarVisibility.value = visibility
        _progressBarTextViewVisibility.value = visibility
        _exampleButtonEnable.value = !visible
    }

    /**
     * Initialize variables in permission theory fragment based on permission ID.
     */
    fun initPermissionTexts(context: Context): PermissionVMInit {
        progressBarOn(false)
        var theoryText = ""
        var permissionType = ""
        var permissionText = ""
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
        progressBarOn(true)
        _successServerCommunication.value = waiting
        communicationService.testConnectionToServer(SettingsSharedPreferences(context).getIpSettings(),
            object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    if (!dataIsSent) {
                        createUserInServer(activity, context)
                    } else {
                        _successServerCommunication.value = ok
                        progressBarOn(false)
                    }
                }

                override fun onError() {
                    _successServerCommunication.value = error
                    progressBarOn(false)
                }
            })
    }

    /**
     * Create user account in server.
     */
    private fun createUserInServer(activity: Activity, context: Context) {
        if (userCreatedInServer) {
            addPermissionTypeToServer(activity, context)
        } else {
            communicationService.createUserInServer(
                object : CommunicationService.VolleyStringResponse {
                    override fun onSuccess() {
                        userCreatedInServer = true
                        addPermissionTypeToServer(activity, context)
                    }

                    override fun onError() {
                        _successServerCommunication.value = error
                        progressBarOn(false)
                    }
                })
        }
    }

    /**
     * Create table for user permission data in server.
     */
    private fun addPermissionTypeToServer(activity: Activity, context: Context) {
        communicationService.addPermissionTypeToServer(
            permissionFunction.getPermissionTypeFromPermissionID(
                permissionId
            ), object : CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    sendPermissionDataToServer(activity, context)
                }

                override fun onError() {
                    _successServerCommunication.value = error
                    progressBarOn(false)
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
        communicationService.addCameraPhotoToServer(cameraPhoto, volleyAddDataToServerResponse)
        _successServerCommunication.value = ok
        dataIsSent = true
    }

    /**
     * Delete user account and all users data in server.
     */
    fun deleteUserInServer() {
        if (userCreatedInServer) {
            communicationService.deleteUserInServer(object :
                CommunicationService.VolleyStringResponse {
                override fun onSuccess() {
                    userCreatedInServer = false
                }

                override fun onError() {
                    userCreatedInServer = true

                }

            })
        }

    }

    /**
     * Delete table for user permission data in server.
     */
    fun deleteUserTableInServer() {
        communicationService.deletePermissionTypeInServer(
            permissionFunction.getPermissionTypeFromPermissionID(
                permissionId
            )
        )
        dataIsSent = false
        photo = null
    }

    override fun onCleared() {
        super.onCleared()
        sharedPreferences.addUserCreatedState(userCreatedInServer)
    }
}

