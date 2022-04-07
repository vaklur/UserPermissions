package com.example.userpermissions.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.userpermissions.R
import com.example.userpermissions.permission.permission_types.calendar_permission.CalendarFunction
import com.example.userpermissions.permission.permission_types.call_log_permission.CallLogFunction
import com.example.userpermissions.permission.permission_types.contact_permission.ContactFunction
import com.example.userpermissions.permission.permission_types.location_permission.LocationFunction
import com.example.userpermissions.permission.permission_types.phone_state_permission.PhoneStateFunction
import com.example.userpermissions.permission.permission_types.sms_permission.SmsFunction
import com.example.userpermissions.permission.permission_types.storage_permission.StorageFunction
import com.example.userpermissions.settings.SettingsSharPref
import com.example.userpermissions.volley_communication.CommunicationFunction

/**
 * View model for managing permission fragments data.
 */
class PermissionViewModel:ViewModel() {

    data class PermissionVMInit(var serverAddress:String, val theoryText:String, val permissionType:String, val permissionText:String)

    // ID of permission
    private var permissionId:Int
    // Control if data was send to server
    private var dataIsSend:Boolean
    // save photo from camera for offline example
    private var photo:Bitmap? = null
    // User created in server
    private var userCreatedInServer:Boolean

    private val comFun:CommunicationFunction
    private val permissionFun:PermissionFunction

    private var theoryText:String
    private var permissionType:String
    private var permissionText:String


    init {
        permissionId = 0
        dataIsSend = false
        userCreatedInServer = false
        comFun = CommunicationFunction()
        permissionFun = PermissionFunction()
        theoryText = ""
        permissionType = ""
        permissionText = ""
    }

    // Getter and Setters
    fun savePhoto (newPhoto:Bitmap){
        photo=newPhoto
    }

    fun getPhoto(): Bitmap? {
        return photo
    }

    fun savePermissionID (newPermissionId:Int){
        permissionId = newPermissionId
    }

    fun getPermissionID ():Int{
        return permissionId
    }

    fun saveDataIsSend (newDataIsSend:Boolean){
        dataIsSend = newDataIsSend
    }

    fun getDataIsSend ():Boolean{
        return dataIsSend
    }

    /**
     * Initialize variables in permission theory fragment based on permission ID.
     *
     * @param context Application context.
     */
    fun initPermissionTexts(context: Context):PermissionVMInit{
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
        return PermissionVMInit(SettingsSharPref(context).getIpSettings(),theoryText,permissionType,permissionText)
    }

    /**
     * Function which send permission data to server
     *
     * @param activity Fragment activity.
     * @param context Application context.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun sendDataToServer(activity: Activity, context: Context){
        if (userCreatedInServer){
            sendPermissionDataToServer(activity, context)
        }
        else {
            comFun.createUserInServer(activity,
                object : CommunicationFunction.VolleyStringResponse {
                    override fun onSuccess() {
                        sendPermissionDataToServer(activity, context)
                        userCreatedInServer = true
                    }

                    override fun onError() {

                    }
                })
        }

    }


    /**
     * Send permission data to server based on permission ID.
     *
     * @param activity Fragment activity.
     * @param context Application context.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun sendPermissionDataToServer (activity: Activity, context: Context){
        val permissionTableType = permissionFun.getPermissionTypeFromPermissionID(permissionId)
        comFun.createPermissionTableInServer(activity, permissionTableType)
        when (permissionId) {
            1 -> comFun.addSMStoServer(activity, SmsFunction().readSms( 10,activity.contentResolver,context))
            2 -> comFun.addContactToServer(activity, ContactFunction().readContacts(activity.contentResolver, 10))
            3 -> comFun.addCallLogToServer(activity, CallLogFunction().readCallLogs( 10,activity.contentResolver,context))
            4 -> comFun.addEventToServer(activity, CalendarFunction().readCalendarEvents(activity.contentResolver, 10))
            5 -> LocationFunction().getLastLocation(activity, context)
            6 -> comFun.addMediaPhotoToServer(activity, StorageFunction().getPhotosFromGallery(activity.contentResolver, 10))
            7 -> comFun.addPhoneStateToServer(activity, PhoneStateFunction().getDataFromSIM(context))
            }
        }

    /**
     * Function which delete all user data in server.
     */
    fun deleteUserInServer (activity: Activity){
        comFun.deleteUserInServer(activity)
        userCreatedInServer = false
    }

    /**
     * Function which delete user table in server and clear a View Model permission variables to default
     */
    fun deleteUserTableInServer (activity: Activity){
        comFun.deleteUserTableInServer(permissionFun.getPermissionTypeFromPermissionID(permissionId),activity)
        permissionId=0
        dataIsSend=false
        photo = null
    }


}

